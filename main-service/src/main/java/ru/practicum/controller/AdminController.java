package ru.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto.EventFullDto;
import ru.practicum.dto.EventDto.UpdateEventAdminRequest;
import ru.practicum.dto.UserDto.NewUserRequest;
import ru.practicum.dto.UserDto.UserDto;
import ru.practicum.dto.areaDto.AreaDto;
import ru.practicum.dto.areaDto.NewAreaDto;
import ru.practicum.dto.areaDto.UpdateAreaDto;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.NewCategoryDto;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.compilationDto.UpdateCompilationRequest;
import ru.practicum.model.enums.EventState;
import ru.practicum.service.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final CategoryService categoryService;

    private final UserService userService;

    private final EventService eventService;

    private final CompilationService compilationService;

    private final AreaService areaService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("add new category = {}", newCategoryDto);
        return categoryService.add(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("delete category by id = {}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("update category by id = {} with category = {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("get users by ids {} or by size = {} from {}", ids, size, from);
        return userService.getList(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("add new user = {}", newUserRequest);
        var data = userService.add(newUserRequest);
        log.info("add new user = {}", data);
        return data;
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("delete user by id = {}", userId);
        userService.delete(userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEventsList(@RequestParam(required = false) Long[] users,
                                            @RequestParam(required = false) EventState[] states,
                                            @RequestParam(required = false) Long[] categories,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeEnd,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) throws JsonProcessingException {
        log.info("get events for admin by sorts: users = {}, states = {}, category = {}, rangeStart = {}, rangeEnd = {}," +
                " size = {}, from {}", users, states, categories, rangeStart, rangeEnd, size, from);
        return eventService.getListForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                          @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("update event by id = {} with updates = {}", eventId, updateEventAdminRequest);
        return eventService.updateByAdmin(eventId, updateEventAdminRequest);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("add new compilation = {}", newCompilationDto);
        return compilationService.add(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("delete compilation by id = {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilationByAdmin(@PathVariable Long compId,
                                           @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("update compilation by id = {} with updates = {}", compId, updateCompilationRequest);
        return compilationService.updateByAdmin(compId, updateCompilationRequest);
    }

    @PostMapping("/areas")
    @ResponseStatus(HttpStatus.CREATED)
    public AreaDto addAria(@RequestBody @Valid NewAreaDto newAreaDto) {
        log.info("add area = {}", newAreaDto);
        return areaService.add(newAreaDto);
    }

    @DeleteMapping("/areas/{areaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArea(@PathVariable Long areaId) {
        log.info("delete area by id = {}", areaId);
        areaService.delete(areaId);
    }

    @PatchMapping("/areas/{areaId}")
    public AreaDto updateArea(@PathVariable Long areaId,
                              @RequestBody @Valid UpdateAreaDto updateAreaDto) {
        log.info("update area by id = {} with data = {}", areaId, updateAreaDto);

        return areaService.update(areaId, updateAreaDto);
    }
}
