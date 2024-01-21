package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.FormatterLocalDateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Future;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHit dtoToHit(EndpointHitDto hitDto) {
        EndpointHit hit = EndpointHit.builder().app(hitDto.getApp()).uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(FormatterLocalDateTime.formToDate(hitDto.getTimestamp())).build();
        //time
        return hit;
    }

}
