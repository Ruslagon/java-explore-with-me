package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.EventDto.EventShortDto;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.compilationDto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CompilationMapper {

    @Mapping(target = "confirmedRequests",
            expression = "java( event.getParticipations()!= null ? (int) event.getParticipations().size():0)")
    @Mapping(target = "views", ignore = true)
    protected abstract EventShortDto entityToShortDto(Event event);


    public abstract NewCompilationDto toCompilationDto(NewCompilationDto compilationDto);

    @Mapping(target = "title", expression = "java( compilationDto.getEvents()!= null ? String.valueOf(compilationDto.getEvents().size()):String.valueOf(0))")
    public abstract NewCompilationDto toCompilationDto2(NewCompilationDto compilationDto);

    @Mapping(target = "events", expression = "java(compilation.getEvents().stream().map(this::entityToShortDto).collect(java.util.stream.Collectors.toList()))")
    public abstract CompilationDto entityToDto(Compilation compilation);

    @Mapping(target = "events", source = "events")
    @Mapping(target = "id", ignore = true)
    public abstract Compilation newToEntity(NewCompilationDto newCompilationDto, List<Event> events);

    @Mapping(target = "events", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Compilation updateEntity(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest);
}
