package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.AreaRepository;
import ru.practicum.dto.areaDto.AreaDto;
import ru.practicum.dto.areaDto.NewAreaDto;
import ru.practicum.dto.areaDto.UpdateAreaDto;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.AreaMapper;
import ru.practicum.model.Area;
import ru.practicum.model.enums.SortArea;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    private final AreaMapper areaMapper;

    @Transactional
    public AreaDto add(NewAreaDto newAreaDto) {
        Area area = areaMapper.newToEntity(newAreaDto);

        areaRepository.save(area);

        return areaMapper.entityToDto(area);
    }

    @Transactional
    public void delete(Long areaId) {
        areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + areaId + " was not found."));

        areaRepository.deleteById(areaId);
    }

    @Transactional
    public AreaDto update(Long areaId, UpdateAreaDto updateAreaDto) {
        Area areaToUpdate = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + areaId + " was not found."));

        Area updatedArea = areaMapper.updateToEntity(areaToUpdate, updateAreaDto);
        areaRepository.save(updatedArea);
        return areaMapper.entityToDto(updatedArea);
    }

    public List<AreaDto> getList(SortArea sortArea, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<AreaDto> areaDtos;

        if (sortArea.equals(SortArea.IDS)) {
            areaDtos = areaRepository.findAllByOrderById(pageRequest).stream().map(areaMapper::entityToDto)
                    .collect(Collectors.toList());
        } else {
            areaDtos = areaRepository.findAllByOrderByRadius(pageRequest).stream().map(areaMapper::entityToDto)
                    .collect(Collectors.toList());
        }

        return areaDtos;
    }

    public AreaDto getOne(Long areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id=" + areaId + " was not found."));

        return areaMapper.entityToDto(area);
    }
}
