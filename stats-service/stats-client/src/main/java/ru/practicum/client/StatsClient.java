package ru.practicum.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {

    protected final RestTemplate rest;

    private static final ObjectMapper mapper = new ObjectMapper();

    protected final String app;


    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder,
                       @Value("${stats-app.name}") String app) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
        this.app = app;
    }

    public ResponseEntity<Object> hit(HttpServletRequest request) {
        EndpointHitDto body = EndpointHitDto.builder().uri(request.getRequestURI()).app(app)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsResponse;
        try {
            statsResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return statsResponse;
    }

    public ResponseEntity<String> getStats(LocalDateTime start, LocalDateTime end,
                                                       String[] uris, Boolean unique) {
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
                System.out.println(start.toString());
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

        ResponseEntity<String> statsResponse;
        try {
            statsResponse = rest.exchange("/stats" + urlParameters, HttpMethod.GET, requestEntity, String.class, parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray().toString());
        }
        return prepareGatewayResponse(statsResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<String> prepareGatewayResponse(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public static List<ViewStatsDto> getDataOutOfResponse(ResponseEntity<String> response) throws JsonProcessingException {
        return mapper.readValue(response.getBody(), new TypeReference<List<ViewStatsDto>>(){});
    }

}
