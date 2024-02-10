package ru.practicum.dto.areaDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AreaDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private Float lat;

    private Float lon;

    private Float radius;

}
