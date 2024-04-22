package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CompilationDto {
    private final Long id;
    private final Boolean pinned;
    private final String title;
    private final List<EventShortDto> events;
}