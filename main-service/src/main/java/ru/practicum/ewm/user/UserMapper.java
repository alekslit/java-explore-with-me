package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.UserShortDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(UserDto userDto) {
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        return userDto;
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        List<UserDto> userDtoList = users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());

        return userDtoList;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();

        return userShortDto;
    }
}