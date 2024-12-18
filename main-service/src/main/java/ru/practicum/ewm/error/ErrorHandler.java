package ru.practicum.ewm.error;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NotFoundException e) {
        log.warn("404 Не найдено: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        log.warn("400 Некорректные аргументы запроса: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MissingServletRequestParameterException e) {
        log.warn("400 Параметр отсутствует в запросе: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final BadRequestException e) {
        log.warn("400 Некорректный запрос: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ValidationException e) {
        log.warn("400 Некорректное тело запроса: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final ConditionsNotRespected e) {
        log.warn("409 Конфликт: {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.CONFLICT.value(), e.getMessage(), now, name);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Throwable e) {
        log.warn("500 {}", e.getMessage());
        LocalDateTime now = LocalDateTime.now();
        String name = e.getClass().getSimpleName();
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), now, name);
    }
}
