package ru.practicum.ewm.exception;

public class ForbiddenDateTimeException extends RuntimeException {
    public static final String FORBIDDEN_EVENT_DATE_TIME_MESSAGE = "Некорректная дата проведения события. eventDate = ";
    public static final String FORBIDDEN_EVENT_DATE_TIME_ADVICE = "Дата и время на которые намечено событие не может " +
            "быть раньше, чем через два часа от текущего момента.";
    private final String adviceToUser;

    public ForbiddenDateTimeException(String message, String adviceToUser) {
        super(message);
        this.adviceToUser = adviceToUser;
    }

    public String getAdviceToUser() {
        return adviceToUser;
    }
}