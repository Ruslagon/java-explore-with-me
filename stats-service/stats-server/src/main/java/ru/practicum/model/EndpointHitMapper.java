package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHit dtoToHit(EndpointHitDto hitDto) {
        return EndpointHit.builder().app(hitDto.getApp()).uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp()).build();
    }

}
