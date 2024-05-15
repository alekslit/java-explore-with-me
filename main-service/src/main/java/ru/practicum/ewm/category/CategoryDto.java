package ru.practicum.ewm.category;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@Builder(toBuilder = true)
public final class CategoryDto {
    // идентификатор категории:
    private final Long id;

    // название категории;
    @NotBlank(message = "Название категории (name) не может быть пустым.")
    @Size(min = 1, max = 50, message = "Слишком длинное или слишком короткое название категории. " +
            "Длинна названия категории должна быть от {min} до {max} символов.")
    private final String name;
}