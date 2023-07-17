package ru.practicum.ewm.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationStorageDb;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompilationService implements CompilationServiceIntf {
    private final CompilationStorageDb compilationStorage;
    private final EventService eventService;

    @Autowired
    public CompilationService(CompilationStorageDb compilationStorage, EventService eventService) {
        this.compilationStorage = compilationStorage;
        this.eventService = eventService;
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        if (Objects.nonNull(pinned)) {
            return compilationStorage.findAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
        }
        return compilationStorage.findAll(pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Optional<Compilation> compilation = compilationStorage.findById(compId);
        if (Objects.nonNull(compilation)) {
            return CompilationMapper.toCompilationDto(compilation.get());
        } else {
            throw new NotFoundException(String.format("нет подборки событий с id ", compId));
        }
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        List<EventFullDto> eventsDtos = eventService.getEventsByIds(newCompilationDto.getEvents());
        return CompilationMapper.toCompilationDto(compilationStorage.save(CompilationMapper.fromNewCompilationDto(newCompilationDto, eventsDtos)));
    }

    @Override
    public void delete(long compId) {
        compilationStorage.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationToUpdate = CompilationMapper.fromCompilationDto(getCompilationById(compId));

        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents()).stream()
                    .map(EventMapper::fromEventFullDto).collect(Collectors.toSet());
            if (Objects.isNull(events)) {
                throw new NotFoundException("таких событий нет");
            }
            compilationToUpdate.setEvents(events);
        }
        if (Objects.nonNull(updateCompilationRequest.getPinned())) {
            compilationToUpdate.setPinned(updateCompilationRequest.getPinned());
        }
        if (Objects.nonNull(updateCompilationRequest.getTitle())) {
            compilationToUpdate.setTitle(updateCompilationRequest.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilationStorage.save(compilationToUpdate));
    }
}
