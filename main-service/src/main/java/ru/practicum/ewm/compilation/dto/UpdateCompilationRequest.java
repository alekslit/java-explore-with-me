package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public final class UpdateCompilationRequest {
    // список идентификаторов событий входящих в подборку:
    private final List<Long> events;

    // закреплена ли подборка на главной странице сайта:
    private final Boolean pinned;

    // заголовок подборки:
    @Size(min = 1, max = 50, message = "Слишком длинный или слишком короткий заголовок подборки " +
            "(title). Длинна заголовка подборки должна быть от {min} до {max} символов.")
    private final String title;
}