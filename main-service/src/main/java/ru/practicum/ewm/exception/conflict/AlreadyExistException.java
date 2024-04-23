package ru.practicum.ewm.exception.conflict;

public class AlreadyExistException extends CustomConflictException {
    public static final String DUPLICATE_USER_EMAIL_MESSAGE = "Пользователь с таким email уже существует. email = ";
    public static final String DUPLICATE_USER_EMAIL_ADVICE = "Пожалуйста, замените email.";
    public static final String DUPLICATE_CATEGORY_NAME_MESSAGE = "Категория с таким названием уже существует. name = ";
    public static final String DUPLICATE_CATEGORY_NAME_ADVICE = "Пожалуйста, замените название категории.";
    public static final String DUPLICATE_PARTICIPATION_REQUEST_MESSAGE = "Такой запрос на участие в событии уже " +
            "существует.";
    public static final String DUPLICATE_PARTICIPATION_REQUEST_ADVICE = "Каждый пользователь может отправить только " +
            "один запрос на участие в конкретном событии.";
    public static final String DUPLICATE_COMPILATION_NAME_MESSAGE = "Подборка с таким заголовком уже существует. " +
            "title = ";
    public static final String DUPLICATE_COMPILATION_NAME_ADVICE = "Пожалуйста замените заголовок подборки.";
    public static final String DUPLICATE_COMPLAINT_MESSAGE = "Такая жалоба на комментарий уже существует.";
    public static final String DUPLICATE_COMPLAINT_ADVICE = "Каждый пользователь может отправить только " +
            "одну жалобу на конкретный комментарий.";

    public AlreadyExistException(String message, String adviceToUser) {
        super(message, adviceToUser);
    }
}