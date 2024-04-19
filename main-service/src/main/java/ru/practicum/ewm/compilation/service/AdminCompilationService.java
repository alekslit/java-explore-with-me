package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewOrUpdateCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventParticipationCount;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.AlreadyExistException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_COMPILATION_NAME_ADVICE;
import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_COMPILATION_NAME_MESSAGE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationService implements CompilationService {
    private final StatClient client;

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    /*--------------------Основные методы--------------------*/
    public CompilationDto saveCompilation(NewOrUpdateCompilationDto compilationDto) {
        log.debug("Попытка сохранить объект Compilation.");
        // получаем список событий:
        List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilation(compilationDto, events);
        // сохраняем подборку:
        compilation = saveCompilationObject(compilation);
        // готовим dto для ответа:
        CompilationDto resultCompilation = CompilationMapper.mapToCompilationDto(compilation);
        List<EventShortDto> resultEvents = addViewsAndParticipantCountToEvent(resultCompilation.getEvents());
        resultCompilation.setEvents(resultEvents);

        return resultCompilation;
    }

    public CompilationDto updateCompilation(NewOrUpdateCompilationDto compilationDto, Long compId) {
        log.debug("Попытка обновить объект Compilation.");
        // проверим, существует ли подборка с таким id:
        Compilation compilation = getCompilationById(compId);
        // обновляем объект:
        compilation = updateCompilationObject(compilation, compilationDto);
        compilation = saveCompilationObject(compilation);
        // готовим dto для ответа:
        CompilationDto resultCompilation = CompilationMapper.mapToCompilationDto(compilation);
        List<EventShortDto> resultEvents = addViewsAndParticipantCountToEvent(resultCompilation.getEvents());
        resultCompilation.setEvents(resultEvents);

        return resultCompilation;
    }

    public String deleteCompilation(Long compId) {
        log.debug("Попытка удалить объект Compilation по его id.");
        // проверим, существует ли подборка с таким id:
        getCompilationById(compId);
        // удаляем:
        compilationRepository.deleteById(compId);

        return String.format("Подборка с compId = %d, удалена.", compId);
    }

    /*---------------Вспомогательные методы---------------*/
    private Compilation getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), COMPILATION_NOT_FOUND_MESSAGE, compId);
            return new NotFoundException(COMPILATION_NOT_FOUND_MESSAGE + compId, COMPILATION_NOT_FOUND_ADVICE);
        });

        return compilation;
    }

    private Compilation saveCompilationObject(Compilation compilation) {
        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {}{}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_COMPILATION_NAME_MESSAGE, compilation.getTitle());
            throw new AlreadyExistException(DUPLICATE_COMPILATION_NAME_MESSAGE + compilation.getTitle(),
                    DUPLICATE_COMPILATION_NAME_ADVICE);
        }

        return compilation;
    }

    private Compilation updateCompilationObject(Compilation compilation, NewOrUpdateCompilationDto compilationDto) {
        compilation.setPinned(compilationDto.getPinned() != null ?
                compilationDto.getPinned() : compilation.getPinned());
        compilation.setTitle(compilationDto.getTitle() != null ? compilationDto.getTitle() : compilation.getTitle());
        if (compilationDto.getEvents() != null) {
            // получаем список событий:
            List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            compilation.setEvents(events);
        }

        return compilation;
    }

    private List<EventShortDto> addViewsAndParticipantCountToEvent(List<EventShortDto> eventList) {
        eventList = addViewsToEvent(addParticipantCountToEvent(eventList));

        return eventList;
    }

    private List<EventShortDto> addViewsToEvent(List<EventShortDto> eventList) {
        // собираем uris событий для запроса количества просмотров:
        List<String> eventUris = eventList.stream()
                .map(event -> "events/" + event.getId())
                .collect(Collectors.toList());
        // получаем статистику просмотров:
        List<ViewStats> statsList = client.getStats(null, null, eventUris, false)/*.getBody()*/;
        // добавляем просмотры к событиям:
        eventList = eventList.stream()
                .map(event -> {
                    String eventId = event.getId().toString();
                    for (ViewStats stats : statsList) {
                        String uriId = stats.getUri().substring(stats.getUri().length() - 1);
                        if (eventId.equals(uriId)) {
                            event.setViews(stats.getHits());
                            return event;
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());

        return eventList;
    }

    private List<EventShortDto> addParticipantCountToEvent(List<EventShortDto> eventList) {
        // собираем id-шники событий для запроса количества участников:
        List<Long> eventUris = eventList.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());
        // получаем количество участников:
        List<EventParticipationCount> participationCounts = participationRequestRepository
                .getEventsParticipationCount(eventUris);
        // добавляем количество участников событию:
        eventList = eventList.stream()
                .map(event -> {
                    for (EventParticipationCount count : participationCounts) {
                        if (event.getId().equals(count.getEventId())) {
                            event.setConfirmedRequests(count.getParticipationCount());
                            return event;
                        }
                    }
                    return event;
                })
                .collect(Collectors.toList());

        return eventList;
    }
}