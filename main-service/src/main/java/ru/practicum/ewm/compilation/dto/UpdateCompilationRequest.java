package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    // список идентификаторов событий входящих в подборку:
    private List<Long> events;

    // закреплена ли подборка на главной странице сайта:
    private Boolean pinned;

    // заголовок подборки:
    @Size(min = 1, max = 50, message = "Слишком длинный или слишком короткий заголовок подборки " +
            "(title). Длинна заголовка подборки должна быть от {min} до {max} символов.")
    private String title;
}