package ru.practicum.ewm.category;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class CategoryDto {
    // идентификатор категории:
    private Long id;

    // название категории;
    @NotBlank(message = "Название категории (name) не может быть пустым.")
    @Size(min = 1, max = 50, message = "Слишком длинное или слишком короткое название категории. " +
            "Длинна названия категории должна быть от {min} до {max} символов.")
    private String name;
}