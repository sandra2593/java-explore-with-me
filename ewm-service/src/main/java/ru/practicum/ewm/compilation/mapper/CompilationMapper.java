package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder().id(compilation.getId()).pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList())
                ).build();
    }

    public static Compilation fromCompilationDto(CompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setId(compilationDto.getId());
        compilation.setPinned(compilationDto.isPinned());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(compilationDto.getEvents().stream().map(EventMapper::fromEventShortDto).collect(Collectors.toSet()));
        return compilation;
    }

    public static NewCompilationDto toNewCompilationDto(Compilation compilation) {
        return NewCompilationDto.builder().pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(Event::getId)
                        .collect(Collectors.toList())
                ).build();
    }

    public static Compilation fromNewCompilationDto(NewCompilationDto newCompilationDto, List<EventFullDto> eventFullDtos) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setEvents(eventFullDtos.stream().map(EventMapper::fromEventFullDto).collect(Collectors.toSet()));
        return compilation;

    }
}
