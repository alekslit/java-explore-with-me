package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.AlreadyExistException;

import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_EMAIL_ADVICE;
import static ru.practicum.ewm.exception.AlreadyExistException.DUPLICATE_EMAIL_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User saveUser(UserDto userDto) {
        log.debug("Попытка сохранить новый объект User.");
        User user = UserMapper.mapToUser(userDto);
        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {}{}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_EMAIL_MESSAGE, userDto.getEmail());
            throw new AlreadyExistException(DUPLICATE_EMAIL_MESSAGE + userDto.getEmail(), DUPLICATE_EMAIL_ADVICE);
        }

        return user;
    }
}