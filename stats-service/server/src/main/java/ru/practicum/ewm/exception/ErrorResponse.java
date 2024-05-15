package ru.practicum.ewm.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public final class ErrorResponse {
    private final String error;
    private final String adviceToUser;
}