package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.*;
import ru.practicum.model.*;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.ParticipationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final CategoryRepository categoryRepository;

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestRepository participationRequestRepository;

    private final UserRepository userRepository;

    @Transactional
    public List<ParticipationRequest> testDb() {
        var user1 = User.builder().email("1@mail.ru").name("1").build();
        var user2 = User.builder().email("2@mail.ru").name("2").build();
        var user3 = User.builder().email("3@mail.ru").name("3").build();
        var user4 = User.builder().email("4@mail.ru").name("4").build();

        var userUpd1 = userRepository.save(user1);
        log.info("user1 = {}", userUpd1);
        var userUpd2 = userRepository.save(user2);
        log.info("user2 = {}", userUpd2);
        var userUpd3 = userRepository.save(user3);
        log.info("user3 = {}", userUpd3);
        var userUpd4 = userRepository.save(user4);
        log.info("user4 = {}", userUpd4);

        var category = Category.builder().name("кино").build();
        var catReg = categoryRepository.save(category);
        log.info("catReg = {}", catReg);

        var event = Event.builder().annotation("у галактики есть стражи")
                .category(catReg).createdOn(LocalDateTime.now())
                .description("что-то про галактики её стражей и семью").eventDate(LocalDateTime.now().plusYears(1))
                .publishedOn(LocalDateTime.now().minusDays(2)).initiator(userUpd1)
                .location(Location.builder().lat(2.22f).lon(3.03f).build())
                .requestModeration(true).state(EventState.PUBLISHED).title("стражи галактики")
                .paid(true)
                .participantLimit(3).build();

        var eventReg = eventRepository.save(event);
        log.info("eventReg = {}", eventReg);

        var participation1 = ParticipationRequest.builder().created(LocalDateTime.now())
                .status(ParticipationStatus.PENDING)
                .requester(userUpd2).event(eventReg).build();
        var partReg1 = participationRequestRepository.save(participation1);
        log.info("partReg1 = {}", partReg1);

        var participation2 = ParticipationRequest.builder().created(LocalDateTime.now())
                .status(ParticipationStatus.CONFIRMED)
                .requester(userUpd3).event(eventReg).build();
        var partReg2 = participationRequestRepository.save(participation2);
        log.info("partReg2 = {}", partReg2);

        var participation3 = ParticipationRequest.builder().created(LocalDateTime.now())
                .status(ParticipationStatus.CONFIRMED)
                .requester(userUpd4).event(eventReg).build();
        var partReg3 = participationRequestRepository.save(participation3);
        log.info("partReg3 = {}", partReg3);

        var updatedEve = eventRepository.findById(eventReg.getId());
        log.info("updatedEve = {}", updatedEve);
        //log.info("updatedEveReq = {}", updatedEve.get().getConfirmedRequests());

        List<Event> events = new ArrayList<>();
        events.add(updatedEve.get());

        var comp = Compilation.builder().title("galaxy").pinned(true).events(events).build();
        var compReg = compilationRepository.save(comp);

        log.info("compReg = {}", compReg);

        return eventRepository.findById(eventReg.getId()).get().getParticipations();
    }

    public List<ParticipationRequest> testEvent() {
        log.info("exist = {}", eventRepository.findById(1L).isPresent());
        //log.info("formula data = {}", eventRepository.findById(1L).get().getConfirmedRequests());
        var list = eventRepository.findById(1L).get().getParticipations();
        log.info("list = {} ", list.size());
        log.info("available = {}", eventRepository.findById(1L).get().getIsAvailable());
        return null;
    }
}
