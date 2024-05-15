package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Getter
@ToString
@Builder(toBuilder = true)
public final class CompilationDto {
    private final Long id;
    private final Boolean pinned;
    private final String title;
    private final List<EventShortDto> events;
}