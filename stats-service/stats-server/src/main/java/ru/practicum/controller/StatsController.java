package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> addHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Creating hit = {}", hitDto);
        statsService.addHit(hitDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public List<ViewStats> getHits(@RequestParam String start, @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("получить все stats для фильтрации start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}