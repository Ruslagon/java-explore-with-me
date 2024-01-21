package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EndpointHitDto {

//    @EqualsAndHashCode.Include
//    private Long id;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;
    @NotBlank
    private String ip;

//    @NotNull
//    @PastOrPresent
//    private LocalDateTime timestamp;

    @NotBlank
    private String timestamp;
}
