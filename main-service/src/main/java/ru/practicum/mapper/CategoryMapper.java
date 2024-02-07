package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.NewCategoryDto;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CategoryMapper {

    @Mapping(target = "id", ignore = true)
    public abstract Category newToEntity(NewCategoryDto newCategoryDto);

    public abstract CategoryDto entityToDto(Category Category);

    @Mapping(source = "categoryDto.name", target = "name")
    @Mapping(source = "catId", target = "id")
    public abstract Category dtoAndIdToEntity(CategoryDto categoryDto, Long catId);
}
