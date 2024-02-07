package ru.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto.EventFullDto;
import ru.practicum.dto.EventDto.EventShortDto;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortEvent;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class PublicController {

    private final CategoryService categoryService;

    private final EventService eventService;

    private final CompilationService compilationService;

    @GetMapping("/categories")
    public List<CategoryDto> getListCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("get page of category from = {}, size = {}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("get category by id = {}", catId);
        return categoryService.getOne(catId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getListEvents(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) Long[] categories,
                                             @RequestParam(required = false) Boolean paid,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                             @RequestParam(required = false) Boolean onlyAvailable,
                                             @RequestParam(required = false) SortEvent sort,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                             HttpServletRequest request) throws JsonProcessingException {
        log.info("get events public by sorts: text = {}, category = {}, paid = {}, rangeStart = {}, rangeEnd = {}," +
                " onlyAvailable = {}, sort = {}, size = {}, from {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, size, from);
        return eventService.getListForPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) throws JsonProcessingException {
        log.info("get event by id = {}", id);
        return eventService.getOnePublic(id, request);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getListCompilation(@RequestParam(required = false) Boolean pinned,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("get page of compilation from = {}, size = {}, pinned? = {}", from, size, pinned);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("get compilation by id = {}", compId);
        return compilationService.getOne(compId);
    }
}
