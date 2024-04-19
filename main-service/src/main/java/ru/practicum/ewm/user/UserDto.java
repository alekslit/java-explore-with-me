package ru.practicum.ewm.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    // идентификатор:
    private Long id;

    // имя:
    @Size(min = 2, max = 250, message = "Слишком длинное или слишком короткое имя пользователя. " +
            "Длинна имени должна быть от {min} до {max} символов.")
    private String name;

    // электронная почта:
    @NotNull(message = "Адрес электронной почты не может быть пустым")
    @Size(min = 6, max = 254, message = "Слишком длинный или слишком короткий email пользователя. " +
            "Длинна email должна быть от {min} до {max} символов.")
/*    @Email(regexp = "([A-Za-z0-9]{1,}[\\\\-]{0,1}[A-Za-z0-9]{1,}[\\\\.]{0,1}[A-Za-z0-9]{1,})+@"
            + "([A-Za-z0-9]{1,}[\\\\-]{0,1}[A-Za-z0-9]{1,}[\\\\.]{0,1}[A-Za-z0-9]{1,})+[\\\\.]{1}[a-z]{2,10}",
            message = "Некорректный адрес электронной почты: ${validatedValue}")*/
    private String email;
}