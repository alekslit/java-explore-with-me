package ru.practicum.ewm.exception.conflict;

public class CustomConflictException extends RuntimeException {
    private final String adviceToUser;

    public CustomConflictException(String message, String adviceToUser) {
        super(message);
        this.adviceToUser = adviceToUser;
    }

    public String getAdviceToUser() {
        return adviceToUser;
    }
}