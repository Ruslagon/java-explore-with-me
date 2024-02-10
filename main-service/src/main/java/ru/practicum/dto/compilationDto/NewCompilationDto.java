package ru.practicum.dto.compilationDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned = false;

    @Size(min = 1, max = 50)
    @NotBlank
    private String title;

}
