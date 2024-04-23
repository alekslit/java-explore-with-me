package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static Compilation mapToCompilation(NewCompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned() != null ? compilationDto.getPinned() : false)
                .title(compilationDto.getTitle())
                .events(events)
                .build();
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(EventMapper.mapToEventShortDto(compilation.getEvents()))
                .build();
    }

    public static List<CompilationDto> mapToCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }
}