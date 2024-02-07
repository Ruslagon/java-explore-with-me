package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    String app;

    String uri;

    Integer hits;
}
