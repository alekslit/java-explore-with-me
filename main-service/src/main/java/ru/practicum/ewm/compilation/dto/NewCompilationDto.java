package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NewCompilationDto {
    // список идентификаторов событий входящих в подборку:
    private final List<Long> events;

    // закреплена ли подборка на главной странице сайта:
    private final Boolean pinned;

    // заголовок подборки:
    @NotBlank(message = "Заголовок подборки (title) не может быть пустым.")
    @Size(min = 1, max = 50, message = "Слишком длинный или слишком короткий заголовок подборки " +
            "(title). Длинна заголовка подборки должна быть от {min} до {max} символов.")
    private final String title;
}