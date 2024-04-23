package ru.practicum.ewm.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class UserDto {
    // идентификатор:
    private Long id;

    // имя:
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 250, message = "Слишком длинное или слишком короткое имя пользователя. " +
            "Длинна имени должна быть от {min} до {max} символов.")
    private final String name;

    // электронная почта:
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Size(min = 6, max = 254, message = "Слишком длинный или слишком короткий email пользователя. " +
            "Длинна email должна быть от {min} до {max} символов.")
    @Email(regexp = "\\w{1,64}([\\.]?\\w{1,64})*@\\w{1,64}([\\.]?\\w{1,64})*\\.\\w{1,63}",
            message = "Некорректный адрес электронной почты: ${validatedValue}")
    private final String email;
}