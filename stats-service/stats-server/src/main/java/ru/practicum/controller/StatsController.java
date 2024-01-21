package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity addHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Creating hit {}", hitDto);
        statsService.addHit(hitDto);
//        EndpointHitDto hito = EndpointHitDto.builder().build();
        return ResponseEntity.ok().build();
    }

    //return list
    @GetMapping("/stats")
    public List<ViewStats> getHits(@RequestParam(required = false) String start, @RequestParam(required = false) String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        //log.info("length = {}",uris.length);
        log.info("получить все stats для фильтрации start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        var stats = statsService.getStats(start, end, uris, unique);
        for (ViewStats stat : stats) {
            log.info("data: app={}, uri ={}, hits={} ", stat.getApp(), stat.getUri(), stat.getHits());
        }

        return stats;
    }

    @GetMapping("test/{id}")
    public Object getTest(@PathVariable Long id, HttpServletRequest request, @RequestParam String check) {
        log.info(request.getRemoteAddr());
        log.info(request.getRequestURI());
        return null;
    }

//    @GetMapping("/stats")
//    public List<Object> getData(@RequestParam String start, @RequestParam String end,
//                                          @RequestParam(required = false) String[] uris,
//                                          @RequestParam(defaultValue = "false") Boolean unique) {
//        //ResponseEntity<Object>.of(statsService.getStats(start, end, uris, unique));
//        List<Object> objects = new ArrayList<>();
//        objects.add(null);
//        return objects;
//    }
}
