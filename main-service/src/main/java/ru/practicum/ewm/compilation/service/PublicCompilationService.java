package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.event.dto.EventParticipationCount;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationService implements CompilationService {
    private final StatClient client;

    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    /*--------------------Основные методы--------------------*/
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.debug("Попытка получить список объектов Compilation.");
        // получаем список:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest).getContent();
        // готовим dto для ответа:
        List<CompilationDto> resultCompilations = CompilationMapper.mapToCompilationDto(compilations);
        resultCompilations = resultCompilations.stream()
                .peek(compilation -> compilation.setEvents(addViewsAndParticipantCountToEvent(compilation.getEvents())))
                .collect(Collectors.toList());

        return resultCompilations;
    }

    public CompilationDto getCompilationById(Long compId) {
        log.debug("Попытка объект Compilation по его id.");
        // проверим, существует ли подборка с таким id:
        Compilation compilation = getById(compId);
        // готовим dto для ответа:
        CompilationDto resultCompilation = CompilationMapper.mapToCompilationDto(compilation);
        List<EventShortDto> resultEvents = addViewsAndParticipantCountToEvent(resultCompilation.getEvents());
        resultCompilation.setEvents(resultEvents);

        return resultCompilation;
    }

    /*---------------Вспомогательные методы---------------*/
    private Compilation getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), COMPILATION_NOT_FOUND_MESSAGE, compId);
            return new NotFoundException(COMPILATION_NOT_FOUND_MESSAGE + compId, COMPILATION_NOT_FOUND_ADVICE);
        });

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