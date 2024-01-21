package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;


@Entity
@Table(name = "endpoint")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EndpointHit {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String app;

    @NotBlank
    @Column(nullable = false)
    private String uri;
    @NotBlank
    @Column(nullable = false)
    private String ip;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
