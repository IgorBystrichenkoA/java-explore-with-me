package ru.practicum.ewm.exception;

public class ConditionsNotRespected extends RuntimeException {
    public ConditionsNotRespected(String message) {
        super(message);
    }
}
