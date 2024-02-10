package ru.practicum.dto.areaDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewAreaDto {

    @NotBlank
    @Size(min = 3, max = 250)
    private String name;

    @NotNull
    @Min(-90)
    @Max(90)
    private Float lat;

    @NotNull
    @Min(-180)
    @Max(180)
    private Float lon;

    @NotNull
    @Max(20040)
    @Positive
    private Float radius;
}
