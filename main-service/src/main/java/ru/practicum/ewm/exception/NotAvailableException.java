package ru.practicum.ewm.exception;

public class NotAvailableException extends RuntimeException {
    public static final String NOT_AVAILABLE_DATE_TIME_MESSAGE = "Недопустимый формат дат для поиска событий.";
    public static final String NOT_AVAILABLE_DATE_TIME_ADVICE = "Обратите внимание: " +
            "1. Дата начала диапазона поиска не может быть позже даты конца диапазона поиска.";
    public static final String NOT_AVAILABLE_EVENT_DATE_TIME_MESSAGE = "Некорректная дата проведения события. " +
            "eventDate = ";
    public static final String NOT_AVAILABLE_EVENT_DATE_TIME_ADVICE = "Дата и время на которые намечено событие " +
            "не может быть раньше, чем через два часа от текущего момента.";
    public static final String NOT_AVAILABLE_EVENT_UPDATE_STATUS_MESSAGE = "Недопустимое значение нового " +
            "статуса события. stateAction = ";
    public static final String NOT_AVAILABLE_EVENT_UPDATE_STATUS_ADVICE_TO_ADMIN = "Обратите внимание: " +
            "1. Допустимые значения изменения статуса события: PUBLISH_EVENT, REJECT_EVENT. " +
            "2. Событие можно изменить без изменения его статуса.";
    public static final String NOT_AVAILABLE_EVENT_UPDATE_STATUS_ADVICE_TO_USER = "Обратите внимание: " +
            "1. Допустимые значения изменения статуса события: SEND_TO_REVIEW, CANCEL_REVIEW. " +
            "2. Событие можно изменить без изменения его статуса.";
    public static final String NOT_AVAILABLE_EVENT_SORTED_MESSAGE = "Недопустимое значение сортировки списка " +
            "событий. sort = ";
    public static final String NOT_AVAILABLE_EVENT_SORTED_ADVICE = "Обратите внимание: " +
            "1. Допустимые значения сортировки: EVENT_DATE, VIEWS. " +
            "2. Значение сортировки можно не указывать, если сортировка не требуется.";
    public static final String NOT_AVAILABLE_REQUEST_STATUS_MESSAGE = "Недопустимое значение нового статуса запроса " +
            "на участие в событии. status = ";
    public static final String NOT_AVAILABLE_REQUEST_STATUS_ADVICE = "Обратите внимание: " +
            "1. Допустимые значения нового статуса запроса: CONFIRMED, REJECTED.";

    private final String adviceToUser;

    public NotAvailableException(String message, String adviceToUser) {
        super(message);
        this.adviceToUser = adviceToUser;
    }

    public String getAdviceToUser() {
        return adviceToUser;
    }
}