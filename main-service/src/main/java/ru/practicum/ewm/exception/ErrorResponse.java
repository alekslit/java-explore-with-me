package ru.practicum.ewm.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private final String error;
    private final String adviceToUser;
}