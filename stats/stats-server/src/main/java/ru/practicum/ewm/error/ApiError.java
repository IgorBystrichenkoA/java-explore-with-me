package ru.practicum.ewm.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiError {
    private final String message;
    private final HttpStatus status;
}
