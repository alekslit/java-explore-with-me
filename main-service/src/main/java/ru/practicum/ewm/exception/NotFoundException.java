package ru.practicum.ewm.exception;

public class NotFoundException extends RuntimeException {
    public static final String USER_NOT_FOUND_MESSAGE = "Пользователя с таким id не существует. userId = ";
    public static final String USER_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id пользователя.";
    public static final String CATEGORY_NOT_FOUND_MESSAGE = "Категории с таким id не существует. catId = ";
    public static final String CATEGORY_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id категории.";
    public static final String EVENT_NOT_FOUND_MESSAGE = "События с таким id не существует. eventId = ";
    public static final String EVENT_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id события.";
    public static final String PARTICIPATION_REQUEST_NOT_FOUND_MESSAGE = "Запроса на участие в событии " +
            "с таким id не существует. requestId = ";
    public static final String PARTICIPATION_REQUEST_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id " +
            "запроса на участие в событии.";
    public static final String COMPILATION_NOT_FOUND_MESSAGE = "Подборки с таким id не существует. compId = ";
    public static final String COMPILATION_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id подборки.";
    public static final String COMMENT_NOT_FOUND_MESSAGE = "Комментария с таким id не существует. commentId = ";
    public static final String COMMENT_NOT_FOUND_ADVICE = "Пожалуйста проверьте корректность id комментария.";

    private final String adviceToUser;

    public NotFoundException(String message, String adviceToUser) {
        super(message);
        this.adviceToUser = adviceToUser;
    }

    public String getAdviceToUser() {
        return adviceToUser;
    }
}