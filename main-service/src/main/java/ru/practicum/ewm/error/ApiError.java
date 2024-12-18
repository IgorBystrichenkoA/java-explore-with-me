package ru.practicum.ewm.error;

import java.time.LocalDateTime;

public record ApiError(Integer status, String message, LocalDateTime timestamp, String name) {
}
