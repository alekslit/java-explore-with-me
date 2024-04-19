package ru.practicum.ewm.user;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    List<User> findUsers(List<Long> ids, Integer from, Integer size);

    String deleteUser(Long userId);
}