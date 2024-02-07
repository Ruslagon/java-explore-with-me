package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.EventDto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.StateAction;
import ru.practicum.model.enums.StateActionReview;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class EventMapper {

    @Mapping(target = "confirmedRequests",
        expression = "java( event.getParticipations()!= null ? (int) event.getParticipations().size():0)")
    @Mapping(target = "views", ignore = true)
    public abstract EventShortDto entityToShortDto(Event event);

    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "state", expression = "java(ru.practicum.model.enums.EventState.PENDING)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "participations", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    public abstract Event newDtoToEntity(NewEventDto newEventDto, User initiator, Category category);

    @Mapping(target = "confirmedRequests",
            expression = "java( event.getParticipations()!= null ? (int) event.getParticipations().size():0)")
    @Mapping(target = "views", constant = "0")
    public abstract EventFullDto EntityToFullDto(Event event);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "participations", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "state",
            expression = "java( updateEventUserRequest.getStateAction() != null ? " +
                    "getNewState(updateEventUserRequest.getStateAction()):eventFound.getState())")
    @Mapping(target = "isAvailable", ignore = true)
    public abstract Event updateEntityByUpdateUser(@MappingTarget Event eventFound,
                                                   UpdateEventUserRequest updateEventUserRequest,
                                                   Category category);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "participations", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "state",
            expression = "java( updateEventAdminRequest.getStateAction() != null ? " +
                    "getNewStateAdmin(updateEventAdminRequest.getStateAction()):eventFound.getState())")
    @Mapping(target = "isAvailable", ignore = true)
    public abstract Event updateEntityByUpdateAdmin(@MappingTarget Event eventFound,
                                                    UpdateEventAdminRequest updateEventAdminRequest,
                                                    Category category);

    @Mapping(target = "confirmedRequests",
            expression = "java( event.getParticipations()!= null ? (int) event.getParticipations().size():0)")
    @Mapping(target = "views", constant = "0")
    public abstract EventShortDto entityToShortDtoWithViews(Event event);

    @Mapping(target = "confirmedRequests",
            expression = "java( event.getParticipations()!= null ? (int) event.getParticipations().size():0)")
    @Mapping(target = "views", constant = "0")
    public abstract EventFullDto EntityToFullDtoWithViews(Event event);

    protected EventState getNewState(StateActionReview stateActionReview) {
        if (stateActionReview.equals(StateActionReview.CANCEL_REVIEW)) {
            return EventState.CANCELED;
        } else {
            return EventState.PENDING;
        }
    }

    protected EventState getNewStateAdmin(StateAction stateAction) {
        if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
            return EventState.PUBLISHED;
        } else {
            return EventState.CANCELED;
        }
    }
}
