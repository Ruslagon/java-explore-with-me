package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.CompilationRepository;
import ru.practicum.db.EventRepository;
import ru.practicum.dto.compilationDto.CompilationDto;
import ru.practicum.dto.compilationDto.NewCompilationDto;
import ru.practicum.dto.compilationDto.UpdateCompilationRequest;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;


    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilation;

        if (pinned == null) {
            compilation = compilationRepository.findAll(pageRequest).toList();
        } else {
            compilation = compilationRepository.findAllByPinned(pinned, pageRequest).toList();
        }

        return compilation.stream().map(compilationMapper::entityToDto).collect(Collectors.toList());
    }

    public CompilationDto getOne(Long compId) {
        Compilation compilationFound = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationMapper.entityToDto(compilationFound);
    }

    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        List<Event> events;
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            events = new ArrayList<>();
        } else {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }

        Compilation compilationToSave = compilationMapper.newToEntity(newCompilationDto, events);

        compilationRepository.save(compilationToSave);
        return compilationMapper.entityToDto(compilationToSave);
    }

    @Transactional
    public void delete(Long compId) {
        Compilation compilationFound = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));

        compilationRepository.delete(compilationFound);
    }

    @Transactional
    public CompilationDto updateByAdmin(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFound = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id=" + compId + " was not found"));


        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilationFound.setEvents(events);
        }

        Compilation compilationUpdated = compilationMapper.updateEntity(compilationFound, updateCompilationRequest);
        compilationRepository.save(compilationUpdated);

        return compilationMapper.entityToDto(compilationUpdated);
    }
}
