package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.areaDto.AreaDto;
import ru.practicum.dto.areaDto.NewAreaDto;
import ru.practicum.dto.areaDto.UpdateAreaDto;
import ru.practicum.model.Area;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AreaMapper {

    @Mapping(target = "id", ignore = true)
    public abstract Area newToEntity(NewAreaDto areaDto);

    public abstract AreaDto entityToDto(Area area);

    @Mapping(target = "id", ignore = true)
    public abstract Area updateToEntity(@MappingTarget Area areaToUpdate, UpdateAreaDto updateAreaDto);
}
