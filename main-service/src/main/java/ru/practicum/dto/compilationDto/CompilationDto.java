package ru.practicum.dto.compilationDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.dto.EventDto.EventShortDto;
import ru.practicum.model.Event;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompilationDto {

    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    private String title;

}
