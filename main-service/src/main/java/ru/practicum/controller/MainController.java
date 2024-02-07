package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class MainController {

    private final StatsClient statsClient;

    @GetMapping("/testHit")
    public Object testHit(HttpServletRequest request) {
        return statsClient.hit(request);
    }

    @GetMapping("/testGetStats")
    public Object testStats() throws Exception {
        String[] testString = {"/testHit"};
        var list = statsClient.getStats(LocalDateTime.of(2000,01,01,00,01),
                LocalDateTime.of(2035,01,01,00,01),
                testString, false);
        log.info("kod{}", list.getStatusCode());
        log.info("{}", list.getBody());
        List<ViewStatsDto> listDto = StatsClient.getDataOutOfResponse(list);

        for (ViewStatsDto dto : listDto) {
            log.info("app={}", dto.getApp());
            log.info("uri={}", dto.getUri());
            log.info("hits={}", dto.getHits());
        }

        log.info("{}", list.getBody());

        return listDto;
    }
}
