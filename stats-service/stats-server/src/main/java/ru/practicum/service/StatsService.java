package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.db.StatsRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.FormatterLocalDateTime;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.model.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

//    public StatsService() {
//    }

    private final StatsRepository statsRepository;
    @Transactional
    public EndpointHit addHit(EndpointHitDto hitDto) {
        var check = statsRepository.save(EndpointHitMapper.dtoToHit(hitDto));
        log.info("saved = {}", check);
        return check;
    }

    public List<ViewStats> getStats(String start, String end, String[] uris, Boolean unique) {
//        log.info(start);
//        String encode = URLEncoder.encode(start, StandardCharsets.UTF_8);
//        log.info(encode);
//        encode = URLEncoder.encode(encode, StandardCharsets.UTF_8);
//        log.info(encode);

        LocalDateTime startDate = FormatterLocalDateTime.formToDate(start);
        LocalDateTime endDate = FormatterLocalDateTime.formToDate(end);
//        log.info(startDate.toString());
//        encode = URLEncoder.encode(startDate.toString(), StandardCharsets.UTF_8);
//        log.info(encode);
        log.info("got here");
        if (uris == null) {
            if (unique) {
                log.info("got here 1");
                return statsRepository.getStatsWithoutUrisAndUniqueIps(startDate, endDate);
            } else {
                log.info("got here 2");
                return statsRepository.getStatsWithoutUrisAndNotUniqueIps(startDate, endDate);
            }
        } else {
            if (unique) {
                log.info("got here 3");
                return statsRepository.getStatsWithUrisAndUniqueIps(uris, startDate, endDate);
            } else {
                log.info("got here 4");
                return statsRepository.getStatsWithUrisAndNotUniqueIps(uris, startDate, endDate);
            }
        }
    }
}
