package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.FormatterLocalDateTime;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClient2 {

    protected final RestTemplate rest;

    protected final String app;

//    public StatsClient(RestTemplate rest) {
//        this.rest = rest;
//    }

    @Autowired
    public StatsClient2(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder,
                        @Value("${stats-temp.name}") String app) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
        this.app = app;
    }

    public ResponseEntity<Object> hit(HttpServletRequest request) {
        EndpointHitDto body = EndpointHitDto.builder().uri(request.getRequestURI()).app(app)
                .ip(request.getRemoteAddr())
                .timestamp(FormatterLocalDateTime.dateToText(LocalDateTime.now()))
                .build();



        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity statsResponse;
        try {
            statsResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, ResponseEntity.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return statsResponse;
    }

    public ResponseEntity<List> getStats(LocalDateTime start, LocalDateTime end,
                                                       String[] uris, Boolean unique) throws Exception {
//        EndpointHitDto body = EndpointHitDto.builder().uri(request.getRequestURI()).app(app)
//                .ip(request.getRemoteAddr())
//                .timestamp(FormatterLocalDateTime.dateToText(LocalDateTime.now()))
//                .build();




        var det = HttpMethod.GET;
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(defaultHeaders());

        String urlParameters;
        Map<String, Object> parameters;
        if (uris != null) {
            if (unique) {
                urlParameters = "?start={start}&end={end}&uris={uris}&unique={unique}";
                parameters = Map.of(
                        "start", FormatterLocalDateTime.dateToText(start),
                        "end",FormatterLocalDateTime.dateToText(end),
                        "uris", uris,
                        "unique", true);
            } else {
                urlParameters = "?start={start}&end={end}&uris={uris}";
                parameters = Map.of("start", FormatterLocalDateTime.dateToText(start),
                        "end", FormatterLocalDateTime.dateToText(end),
                        "uris", uris);
            }
        } else {
            if (unique) {
                urlParameters = "?start={start}&end={end}&unique={unique}";
                parameters = Map.of(
                        "start", FormatterLocalDateTime.dateToText(start),
                        "end", FormatterLocalDateTime.dateToText(end),
                        "unique", true);
            } else {
                urlParameters = "?start={start}&end={end}";
                parameters = Map.of(
                        "start", FormatterLocalDateTime.dateToText(start),
                        "end", FormatterLocalDateTime.dateToText(end));
            }
        }



        ResponseEntity statsResponse;
        try {
            statsResponse = rest.exchange("/stats" + urlParameters, HttpMethod.GET, requestEntity, Object.class, parameters);
//            if (uris.length == 0) {
//                Map<String, Object> parameters = Map.of(
//                        "uris", uris,
//                        "start", FormatterLocalDateTime.dateToText(start),
//                        "end",FormatterLocalDateTime.dateToText(end),
//                        "unique",
//                );
//                statsResponse = rest.exchange("/stats", HttpMethod.GET, requestEntity, List.class);
//            } else {
//                Map<String, Object> parameters = Map.of(
//                        "uris", uris,
//                        "start", FormatterLocalDateTime.dateToText(start),
//                        "end",FormatterLocalDateTime.dateToText(end)
//                );
//                statsResponse = rest.exchange("/stats", HttpMethod.GET, requestEntity, List.class, parameters);
//            }
        } catch (HttpStatusCodeException e) {
            throw new Exception("код" + e.getStatusCode().toString() + "причина" +  e.getCause().toString());
        }
        return prepareGatewayResponse(statsResponse);
    }

//    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
//                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
//        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
//
//        ResponseEntity<Object> shareitServerResponse;
//        try {
//            if (parameters != null) {
//                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
//            } else {
//                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
//            }
//        } catch (HttpStatusCodeException e) {
//            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
//        }
//        return prepareGatewayResponse(shareitServerResponse);
//    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

}
