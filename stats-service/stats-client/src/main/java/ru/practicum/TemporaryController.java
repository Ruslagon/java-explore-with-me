package ru.practicum;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient2;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class TemporaryController {

    StatsClient2 statsClient2;

    @GetMapping("/testHit")
    public Object testHit(HttpServletRequest request) {
        return statsClient2.hit(request);
    }

    @GetMapping("/testGetStats")
    public Object testStats() throws Exception {
        String[] testString = {"/testHit"};
        var list = statsClient2.getStats(LocalDateTime.of(2000,01,01,00,01),
                LocalDateTime.of(2035,01,01,00,01),
                testString, true);
        log.info("kod{}", list.getStatusCode());
        var mapper = new ObjectMapper();
        log.info("{}", list.getBody().toString());
        List<ViewStatsDto> listDto = mapper.readValue(list.getBody().toString(), new TypeReference<List<ViewStatsDto>>(){});

//        for (Object viewStatsDto : list.getBody()) {
//            ViewStatsDto dto = new ObjectMapper().readValue((String) viewStatsDto, new TypeReference<ViewStatsDto>(){}) {
//            });
//            listDto.add(dto);
//        }
//        for (ViewStatsDto dto : listDto) {
//            log.info("app={}", dto.getApp());
//            log.info("uri={}", dto.getUri());
//            log.info("hits={}", dto.getHits());
//        }
        //new ObjectMapper().readValue(list.getBody(), ViewStatsDto.class);

        log.info("{}", list.getBody());
        log.info("{}",list.getBody().get(0));

//        if (list.getBody() != null) {
//            for (ViewStatsDto viewStatsDto : list.getBody()) {
//                log.info("app={}", viewStatsDto.getApp());
//                log.info("uri={}", viewStatsDto.getUri());
//                log.info("hits={}", viewStatsDto.getHits());
//            }
//        }
        return list.getBody();
    }
}
