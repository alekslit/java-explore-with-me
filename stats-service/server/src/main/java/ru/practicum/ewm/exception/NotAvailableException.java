package ru.practicum.ewm.exception;

public class NotAvailableException extends RuntimeException {
    public static final String NOT_AVAILABLE_DATE_TIME_MESSAGE = "Недопустимый формат дат для поиска статистики.";
    public static final String NOT_AVAILABLE_DATE_TIME_ADVICE = "Обратите внимание: " +
            "1. Дата начала диапазона поиска не может быть позже даты конца диапазона поиска.";

    private final String adviceToUser;

    public NotAvailableException(String message, String adviceToUser) {
        super(message);
        this.adviceToUser = adviceToUser;
    }

    public String getAdviceToUser() {
        return adviceToUser;
    }
}