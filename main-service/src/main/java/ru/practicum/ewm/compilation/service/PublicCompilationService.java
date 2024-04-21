package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.COMPILATION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationService implements CompilationService {
    private final CompilationRepository compilationRepository;

    /*--------------------Основные методы--------------------*/
    public List<Compilation> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.debug("Попытка получить список объектов Compilation.");
        // получаем список:
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations = pinned == null ?
                compilationRepository.findAll(pageRequest).getContent() :
                compilationRepository.findAllByPinned(pinned, pageRequest).getContent();

        return compilations;
    }

    public Compilation getCompilationById(Long compId) {
        log.debug("Попытка объект Compilation по его id.");
        // проверим, существует ли подборка с таким id:
        Compilation compilation = getById(compId);

        return compilation;
    }

    /*---------------Вспомогательные методы---------------*/
    private Compilation getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), COMPILATION_NOT_FOUND_MESSAGE, compId);
            return new NotFoundException(COMPILATION_NOT_FOUND_MESSAGE + compId, COMPILATION_NOT_FOUND_ADVICE);
        });

        return compilation;
    }
}