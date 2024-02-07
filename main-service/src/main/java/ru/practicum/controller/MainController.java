package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.service.TestService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class MainController {

    private final StatsClient statsClient;

    private final TestService testService;

    private final CompilationMapper mapper;

    @GetMapping("/testHit")
    public Object testHit(HttpServletRequest request) {
        return statsClient.hit(request);
    }

    @GetMapping("/testGetStats")
    public Object testStats() throws Exception {
        String[] testString = {"/testHit"};
        log.info(LocalDateTime.of(2000,01,01,00,01).toString());
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

    @GetMapping("/testDB")
    public Object testDb() throws Exception {
        return testService.testDb();
//        return null;
    }

    @GetMapping("/testEvent")
    public List<ParticipationRequest> testEvent() throws Exception {
        return testService.testEvent();
//        return null;
    }

    @PostMapping("/testDefault")
    public Object check(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("newComp = {}", newCompilationDto);
        return newCompilationDto;
    }

    @PostMapping("/testMap1")
    public Object testMap1(@RequestBody NewCompilationDto NewCompilationDto) {
        log.info("newComp = {}", NewCompilationDto);
        log.info("mapped = {}", mapper.toCompilationDto(NewCompilationDto));
        return mapper.toCompilationDto(NewCompilationDto);
    }

    @PostMapping("/testMap2")
    public Object testMap2(@RequestBody NewCompilationDto CompilationDto) {
        log.info("newComp = {}", CompilationDto);
        log.info("mapped = {}", mapper.toCompilationDto2(CompilationDto));
        return mapper.toCompilationDto2(CompilationDto);
    }

    @GetMapping("/testEx/{id}")
    public Object testException(@PathVariable Long id) {
        if (id > 0) {
            throw new EntityNotFoundException("Compilation with id=" + id + " was not found");
        }
        return null;
    }
}
