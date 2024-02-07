package ru.practicum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.StatsClient;
import ru.practicum.db.CategoryRepository;
import ru.practicum.db.EventRepository;
import ru.practicum.db.UserRepository;
import ru.practicum.dto.EventDto.*;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.model.BadRequest;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortEvent;
import ru.practicum.model.enums.StateAction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    private final StatsClient statsClient;

    public List<EventShortDto> getListByUserId(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream().map(eventMapper::entityToShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto add(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + newEventDto.getCategory()
                        + " was not found."));

        Event eventToSave = eventMapper.newDtoToEntity(newEventDto, initiator, category);

        return eventMapper.EntityToFullDto(eventRepository.save(eventToSave));
    }

    public EventFullDto getByIdByUserId(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));

        Event eventFound = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                eventId + " was not found."));

        return eventMapper.EntityToFullDto(eventFound);
    }

    @Transactional
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));

        Event eventFound = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                eventId + " was not found."));

        Category category;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" +
                            updateEventUserRequest.getCategory() + " was not found."));
        } else {
            category = eventFound.getCategory();
        }

        checkUpdateAbilityForUser(eventFound);

        Event updatedEvent = eventMapper.updateEntityByUpdateUser(eventFound, updateEventUserRequest, category);


        log.info("anat = {}, cat = {}, paid = {}",updatedEvent.getAnnotation(), updatedEvent.getCategory(), updatedEvent.getPaid());






        eventRepository.save(updatedEvent);

        return eventMapper.EntityToFullDto(eventFound);
    }

    public List<EventFullDto> getListForAdmin(Long[] users, EventState[] states, Long[] categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                            Integer size) throws JsonProcessingException {
        checkRange(rangeStart, rangeEnd);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events;


        QEvent qEvent = QEvent.event;
        List<BooleanExpression> expressionList = new ArrayList<>();
        if (users != null) {
            expressionList.add(qEvent.initiator.id.in(users));
        }
        if (states != null) {
            expressionList.add(qEvent.state.in(states));
        }
        if (categories != null) {
            expressionList.add(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            expressionList.add(qEvent.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            expressionList.add(qEvent.eventDate.before(rangeEnd));
        }
        if (expressionList.isEmpty()) {
            events = eventRepository.findAll(pageRequest)
                    //.map(eventMapper::EntityToFullDto)
                    .getContent();
        } else {
            BooleanExpression booleanExpression = expressionList.get(0);
            for (int i = 1; i < expressionList.size(); i++) {
                booleanExpression = booleanExpression.and(expressionList.get(i));
            }
            events = eventRepository.findAll(booleanExpression, pageRequest)
                    //.map(eventMapper::EntityToFullDto)
                    .getContent();
        }

        log.info("size = {}", events.size());

        String[] uris = getUrisForStats(events);
        ResponseEntity<String> response = statsClient.getStats(getOldestPublishingDate(events).minusSeconds(10),
                LocalDateTime.now().plusSeconds(10),
                uris, true);

        List<ViewStatsDto> viewStatsDtos = StatsClient.getDataOutOfResponse(response);

        List<EventFullDto> eventsFull = events.stream().map(eventMapper::EntityToFullDto)
                .collect(Collectors.toList());

        eventsFull.forEach(eventFullDto -> {
            for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                if (("/events/" + eventFullDto.getId()).equals(viewStatsDto.getUri())) {
                    eventFullDto.setViews(viewStatsDto.getHits());
                }
            }
        });

        return eventsFull;
    }

    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventFound = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                eventId + " was not found."));

        checkUpdateAbilityForAdmin(eventFound, updateEventAdminRequest);

        Category category;
        if (updateEventAdminRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" +
                            updateEventAdminRequest.getCategory() + " was not found."));
        } else {
            category = eventFound.getCategory();
        }

        Event updatedEvent = eventMapper.updateEntityByUpdateAdmin(eventFound, updateEventAdminRequest, category);

        if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            updatedEvent.setPublishedOn(LocalDateTime.now());
        }

        eventRepository.save(updatedEvent);

        return eventMapper.EntityToFullDto(eventFound);
    }

    @Transactional//hz
    public List<EventShortDto> getListForPublic(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable,SortEvent sort,
                                             Integer from, Integer size, HttpServletRequest request) throws JsonProcessingException {
        statsClient.hit(request);
        checkRange(rangeStart, rangeEnd);
        QEvent qEvent = QEvent.event;
        PageRequest pageRequest;
        if(sort != null && sort.equals(SortEvent.EVENT_DATE)) {
            var sorting = new QSort(qEvent.eventDate.asc());
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sorting);
        } else {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        }


        List<BooleanExpression> expressionList = new ArrayList<>();
        expressionList.add(qEvent.state.eq(EventState.PUBLISHED));
        if (text != null) {
            expressionList.add(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            expressionList.add(qEvent.category.id.in(categories));
        }
        if (paid != null) {
            expressionList.add(qEvent.paid.eq(paid));
        }
        if (rangeStart != null) {
            expressionList.add(qEvent.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            expressionList.add(qEvent.eventDate.before(rangeEnd));
        }
        if (rangeStart == null && rangeEnd == null) {
            expressionList.add(qEvent.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable != null && onlyAvailable) {
            qEvent.participantLimit.eq(0).or(qEvent.isAvailable);
        }

        BooleanExpression booleanExpression = expressionList.get(0);
        for (int i = 1; i < expressionList.size(); i++) {
            booleanExpression = booleanExpression.and(expressionList.get(i));
        }

        if (sort != null && sort.equals(SortEvent.VIEWS)) {
            List<Event> events = StreamSupport.stream(eventRepository.findAll(booleanExpression).spliterator(), false)
                    .collect(Collectors.toList());

            if (events.isEmpty()) {
                return new ArrayList<>();
            }

            String[] uris = getUrisForStats(events);
            ResponseEntity<String> response = statsClient.getStats(getOldestPublishingDate(events).minusSeconds(10),
                    LocalDateTime.now().plusSeconds(10),
                    uris, true);

            List<ViewStatsDto> viewStatsDtos = StatsClient.getDataOutOfResponse(response);

            List<EventShortDto> eventsShort = events.stream().map(eventMapper::entityToShortDtoWithViews)
                    .collect(Collectors.toList());

            eventsShort.forEach(eventShortDto -> {
                for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                    if (("/events/" + eventShortDto.getId()).equals(viewStatsDto.getUri())) {
                        eventShortDto.setViews(viewStatsDto.getHits());
                    }
                }
            });

            eventsShort = eventsShort.stream().sorted((eventShort1, eventShort2) -> {
                if (!eventShort1.getViews().equals(eventShort2.getViews())) {
                    return eventShort1.getViews().compareTo(eventShort2.getViews());
                }
                return eventShort1.getId().compareTo(eventShort2.getId());
            }).collect(Collectors.toList());

            List<EventShortDto> eventShortDtoReturn = new ArrayList<>();
            for (int i = from * size; i > ((from + 1) * size); i++) {
                if (eventsShort.size() > i) {
                    eventShortDtoReturn.add(eventsShort.get(i));
                }
            }

            return eventShortDtoReturn;
        }

        List<Event> events = eventRepository.findAll(booleanExpression, pageRequest).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        String[] uris = getUrisForStats(events);
        ResponseEntity<String> response = statsClient.getStats(getOldestPublishingDate(events).minusSeconds(10),
                LocalDateTime.now().plusSeconds(10), uris, true);

        List<ViewStatsDto> viewStatsDtos = StatsClient.getDataOutOfResponse(response);
        List<EventShortDto> eventsShort = events.stream().map(eventMapper::entityToShortDtoWithViews).collect(Collectors.toList());

        eventsShort.forEach(eventShortDto -> {
            for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                if (("events/" + eventShortDto.getId()).equals(viewStatsDto.getUri())) {
                    eventShortDto.setViews(viewStatsDto.getHits());
                }
            }
        });

        return eventsShort;
        //return eventRepository.findAll(booleanExpression, pageRequest).map(eventMapper::EntityToFullDto).getContent();
        //return eventService.getListForPublic(text, category, paid, rangeStart, rangeEnd, onlyAvailable, sort, size, from);
    }

    @Transactional//hz
    public EventFullDto getOnePublic(Long id, HttpServletRequest request) throws JsonProcessingException {
        //statsClient.hit(request);
        Event eventFound = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" +
                        id + " was not found."));

        String[] oneId =  {"/events/" + id};
        ResponseEntity<String> response = statsClient.getStats(eventFound.getPublishedOn().minusSeconds(10),
                LocalDateTime.now().plusSeconds(10), oneId, true);

        List<ViewStatsDto> oneOrLessStat = StatsClient.getDataOutOfResponse(response);
        log.info("stats = {}", oneOrLessStat);

        EventFullDto returnEvent = eventMapper.EntityToFullDtoWithViews(eventFound);


        if (!oneOrLessStat.isEmpty()) {
            returnEvent.setViews(oneOrLessStat.get(0).getHits());
        }

        statsClient.hit(request);

        return returnEvent;
    }

    private LocalDateTime getOldestPublishingDate(List<Event> events) {
        LocalDateTime oldestPublishingDate = LocalDateTime.now();
        for (Event event : events) {
            if (event.getPublishedOn() != null && event.getPublishedOn().isBefore(oldestPublishingDate)) {
                oldestPublishingDate = event.getPublishedOn();
            }
        }
        return oldestPublishingDate;
    }

    private String[] getUrisForStats(List<Event> events) {
        String[] uris = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            uris[i] = "/events/" + events.get(i).getId();
        }
        return uris;
    }

    private void checkUpdateAbilityForUser(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("dateTime needs to be at least two hours in Future");
        }
        if (!(event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
    }

    private void checkUpdateAbilityForAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("dateTime needs to be at least one hours in Future");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState().name());
                }
            }
            if ((updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT))) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Cannot reject the event because it's not in the right state: " + event.getState().name());
                }
            }
        }
    }

    private void checkRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart == null || rangeEnd == null) {
            return;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequest("rangeStart should be before rangeEnd");
        }
    }
}
