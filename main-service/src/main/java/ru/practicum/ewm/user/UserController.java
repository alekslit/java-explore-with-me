package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    private final UserService service;

    /*--------------------Основные Admin методы--------------------*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.mapToUserDto(service.saveUser(userDto));
    }

    @GetMapping
    public List<UserDto> findUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "Параметр запроса from, должен быть " +
                    "положительным числом или нулём.") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "Параметр запроса size, должен быть " +
                    "положительным числом.") Integer size) {
        return UserMapper.mapToUserDto(service.findUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteUser(@PathVariable Long userId) {
        return service.deleteUser(userId);
    }
}