package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.conflict.AlreadyExistException;

import java.util.List;

import static ru.practicum.ewm.exception.NotFoundException.USER_NOT_FOUND_ADVICE;
import static ru.practicum.ewm.exception.NotFoundException.USER_NOT_FOUND_MESSAGE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_USER_EMAIL_ADVICE;
import static ru.practicum.ewm.exception.conflict.AlreadyExistException.DUPLICATE_USER_EMAIL_MESSAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    /*--------------------Основные методы--------------------*/
    @Override
    public User saveUser(UserDto userDto) {
        log.debug("Попытка сохранить новый объект User.");
        User user = UserMapper.mapToUser(userDto);
        try {
            user = repository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.debug("{}: {}{}.", AlreadyExistException.class.getSimpleName(),
                    DUPLICATE_USER_EMAIL_MESSAGE, userDto.getEmail());
            throw new AlreadyExistException(DUPLICATE_USER_EMAIL_MESSAGE + userDto.getEmail(),
                    DUPLICATE_USER_EMAIL_ADVICE);
        }

        return user;
    }

    @Override
    public List<User> findUsers(List<Long> ids, Integer from, Integer size) {
        log.debug("Попытка получить список объектов User по заданным параметрам.");
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return repository.findUsers(ids, pageRequest).getContent();
    }

    @Override
    public String deleteUser(Long userId) {
        log.debug("Попытка удалить объект User по его Id.");
        // проверим, существует ли такой пользователь:
        getUserById(userId);
        repository.deleteById(userId);

        return String.format("Пользователь с id = %d, удалён.", userId);
    }

    /*---------------Вспомогательные методы---------------*/
    private void getUserById(Long userId) {
        repository.findById(userId).orElseThrow(() -> {
            log.debug("{}: {}{}.", NotFoundException.class.getSimpleName(), USER_NOT_FOUND_MESSAGE, userId);
            return new NotFoundException(USER_NOT_FOUND_MESSAGE + userId, USER_NOT_FOUND_ADVICE);
        });
    }
}