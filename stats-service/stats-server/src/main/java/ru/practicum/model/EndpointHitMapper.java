package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.FormatterLocalDateTime;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHit dtoToHit(EndpointHitDto hitDto) {
        return EndpointHit.builder().app(hitDto.getApp()).uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(FormatterLocalDateTime.formToDate(hitDto.getTimestamp())).build();
    }

}
