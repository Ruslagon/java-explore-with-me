package ru.practicum.dto.participationRequestDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.model.enums.ParticipationStatus;
import ru.practicum.validation.CanNotBePending;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventRequestStatusUpdateRequest {

    @NotNull
    private List<Long> requestIds;

    @NotNull
    @CanNotBePending
    private ParticipationStatus status;

}
