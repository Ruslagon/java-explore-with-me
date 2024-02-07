package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto.EventFullDto;
import ru.practicum.dto.EventDto.EventShortDto;
import ru.practicum.dto.EventDto.NewEventDto;
import ru.practicum.dto.EventDto.UpdateEventUserRequest;
import ru.practicum.dto.participationRequestDto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequestDto.EventRequestStatusUpdateResult;
import ru.practicum.dto.participationRequestDto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Validated
public class PrivateController {

    private final EventService eventService;

    private final ParticipationRequestService participationRequestService;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("get user's event by user id {} by size = {} from {}", userId, size, from);
        return eventService.getListByUserId(userId, from, size);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info("add new event = {} by user (id = {})", newEventDto, userId);
        return eventService.add(userId, newEventDto);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        log.info("get user's event by id {} and user id {}", eventId, userId);
        return eventService.getByIdByUserId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("update event by userId = {}, by id = {} , with data = {}", userId, eventId, updateEventUserRequest);
        return eventService.updateByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEvent(@PathVariable Long userId,
                                                      @PathVariable Long eventId) {
        log.info("get user's requests for event by id {} and initiator id {}", eventId, userId);
        return participationRequestService.getListByEventIdForOwner(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationsOfEventByInitiator(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("update requests by userId = {}, for eventId = {} , with data = {}", userId, eventId,
                eventRequestStatusUpdateRequest);
        return participationRequestService.updateParticipationByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestsByRequesterId(@PathVariable Long userId) {
        log.info("get requester's by Id = {} requests for events", userId);
        return participationRequestService.getListByByRequesterId(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("add new request to event by id = {} by user (id = {})", eventId, userId);
        return participationRequestService.add(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUser(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        log.info("cancel request of userId = {}, for requestId = {}", userId, requestId);
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
