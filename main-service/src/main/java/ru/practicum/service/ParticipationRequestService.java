package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.EventRepository;
import ru.practicum.db.ParticipationRequestRepository;
import ru.practicum.db.UserRepository;
import ru.practicum.dto.participationRequestDto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequestDto.EventRequestStatusUpdateResult;
import ru.practicum.dto.participationRequestDto.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.ParticipationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final ParticipationRequestMapper participationRequestMapper;

    public List<ParticipationRequestDto> getListByEventIdForOwner(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("event isn't published, there can not be any requests");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(participationRequestMapper::EntityToDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult updateParticipationByInitiator(Long userId, Long eventId,
                                                                               EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder().build();
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("event isn't published, there can not be any requests");
        }
        if (event.getParticipantLimit().equals(0) || !event.isRequestModeration()) {
            throw new ConflictException("event " + eventId + " doesn't require any moderation");
        }
        if (!event.getParticipantLimit().equals(0) && (event.getParticipations().size() == event.getParticipantLimit())) {
            throw new ConflictException("The participant limit has been reached");
        }

        if (updateRequest.getStatus().equals(ParticipationStatus.CONFIRMED)) {
            if (!event.getParticipantLimit().equals(0) &&
                    (event.getParticipantLimit() < (updateRequest.getRequestIds().size() + event.getParticipations().size()))) {
                throw new ConflictException("The participant limit has been reached");
            }
            Integer newNumberOfParticipants = updateRequest.getRequestIds().size() + event.getParticipations().size();

            List<ParticipationRequest> updatedList = requestRepository
                    .findAllByIdInAndStatus(updateRequest.getRequestIds(), ParticipationStatus.PENDING);

            if (updatedList.size() != updateRequest.getRequestIds().size()) {
                throw new ConflictException("some of requests isn't of pending state or not exist");
            }
            updatedList.forEach(request -> request.setStatus(ParticipationStatus.CONFIRMED));
            requestRepository.saveAll(updatedList);

            result.setConfirmedRequests(updatedList.stream().map(participationRequestMapper::EntityToDto)
                    .collect(Collectors.toList()));

            if (event.getParticipantLimit() == (newNumberOfParticipants)) {
                List<ParticipationRequest> cancelledRequests = requestRepository
                        .findAllByEventIdAndStatus(eventId, ParticipationStatus.PENDING).stream()
                        .filter((eventPending) -> !updateRequest.getRequestIds().contains(eventPending.getId()))
                        .peek((eventPending) -> eventPending.setStatus(ParticipationStatus.REJECTED))
                        .collect(Collectors.toList());
                requestRepository.saveAll(cancelledRequests);
                result.setRejectedRequests(cancelledRequests.stream().map(participationRequestMapper::EntityToDto)
                        .collect(Collectors.toList()));
            }
        } else {
            List<ParticipationRequest> updatedList = requestRepository
                    .findAllByIdInAndStatus(updateRequest.getRequestIds(), ParticipationStatus.PENDING);

            if (updatedList.size() != updateRequest.getRequestIds().size()) {
                throw new ConflictException("some of requests isn't of pending state");
            }

            updatedList.forEach(request -> request.setStatus(ParticipationStatus.REJECTED));
            requestRepository.saveAll(updatedList);

            result.setRejectedRequests(updatedList.stream().map(participationRequestMapper::EntityToDto)
                    .collect(Collectors.toList()));

        }
        return result;
    }

    public List<ParticipationRequestDto> getListByByRequesterId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));

        return requestRepository.findAllByRequesterId(userId).stream().map(participationRequestMapper::EntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto add(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("users are unable to request event that isn't PUBLISHED");
        }
        if (!event.getParticipantLimit().equals(0) && !event.getIsAvailable()) {
            throw new ConflictException("event isn't available");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator is unable to request his event");
        }
        requestRepository.findByEventIdAndRequesterId(eventId, userId).
                ifPresent((request) -> {
                    throw new ConflictException("You can not re-request");
                }
                );


        ParticipationRequest request = ParticipationRequest.builder().requester(user).event(event)
                .created(LocalDateTime.now())
                .status(ParticipationStatus.PENDING).build();

        if (event.getParticipantLimit().equals(0) || !event.isRequestModeration()) {
            request.setStatus(ParticipationStatus.CONFIRMED);
        }
        requestRepository.save(request);
        return participationRequestMapper.EntityToDto(request);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new EntityNotFoundException("The required object was id=" + requestId + " was not found"));

        request.setStatus(ParticipationStatus.REJECTED);
        requestRepository.save(request);
        request.setStatus(ParticipationStatus.CANCELED);
        return participationRequestMapper.EntityToDto(request);
    }
}
