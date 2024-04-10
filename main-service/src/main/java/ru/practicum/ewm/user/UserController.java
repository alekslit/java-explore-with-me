package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService service;
    @PostMapping
    public UserDto saveUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.mapToUserDto(service.saveUser(userDto));
    }

    @GetMapping
    public List<UserDto> findUser(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return null;
    }
}