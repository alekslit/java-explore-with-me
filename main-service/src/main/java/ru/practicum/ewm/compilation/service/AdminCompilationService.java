package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.AlreadyExistException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_COMPILATION_NAME_ADVICE;
import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_COMPILATION_NAME_MESSAGE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationService implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    /*--------------------Основные методы--------------------*/
    public Compilation saveCompilation(NewCompilationDto compilationDto) {
        log.debug("Попытка сохранить объект Compilation.");
        // получаем список событий:
        List<Event> events = new ArrayList<>();
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.mapToCompilation(compilationDto, events);
        // сохраняем подборку:
        compilation = saveCompilationObject(compilation);

        return compilation;
    }

    public Compilation updateCompilation(UpdateCompilationRequest compilationRequest, Long compId) {
        log.debug("Попытка обновить объект Compilation.");
        // проверим, существует ли подборка с таким id:
        Compilation compilation = getCompilationById(compId);
        // обновляем объект:
        compilation = updateCompilationObject(compilation, compilationRequest);
        compilation = saveCompilationObject(compilation);

        return compilation;
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

    private Compilation updateCompilationObject(Compilation compilation, UpdateCompilationRequest compilationRequest) {
        compilation.setPinned(compilationRequest.getPinned() != null ?
                compilationRequest.getPinned() : compilation.getPinned());
        compilation.setTitle(compilationRequest.getTitle() != null ?
                compilationRequest.getTitle() : compilation.getTitle());
        if (compilationRequest.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            if (!compilationRequest.getEvents().isEmpty()) {
                // получаем список событий:
                events = eventRepository.findAllByIdIn(compilationRequest.getEvents());
            }
            compilation.setEvents(events);
        }

        return compilation;
    }
}