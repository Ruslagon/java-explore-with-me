package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.db.CategoryRepository;
import ru.practicum.db.EventRepository;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.categoryDto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category categoryToAdd = categoryMapper.newToEntity(newCategoryDto);

        Category savedCategory = categoryRepository.save(categoryToAdd);

        return categoryMapper.entityToDto(savedCategory);
    }

    @Transactional
    public void delete(Long catId) {
        categoryRepository.findById(catId)
                        .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found."));

        eventRepository.findFirstByCategoryId(catId)
                .ifPresent(
                        (event) -> {
                    throw new ConflictException("The category is not empty");
                }
                );
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found."));
//        if (category.getName().equals(categoryDto.getName())) {
//            throw new ConflictException("Category old name is equal to new");
//        }
//        categoryRepository.findByName(categoryDto.getName())
//                .ifPresent((categoryFound) -> {
//                    throw new ConflictException("name " + categoryDto.getName() + " is not unique");
//                }
//                );

        Category updatedCat = categoryRepository.save(categoryMapper.dtoAndIdToEntity(categoryDto, catId));

        return categoryMapper.entityToDto(updatedCat);
    }

    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepository.findAll(pageRequest).stream().map(categoryMapper::entityToDto).collect(Collectors.toList());
    }

    public CategoryDto getOne(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId + " was not found."));

        return categoryMapper.entityToDto(category);
    }
}
