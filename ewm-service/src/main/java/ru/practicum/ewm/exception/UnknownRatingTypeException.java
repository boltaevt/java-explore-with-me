package ru.practicum.ewm.exception;

public class UnknownRatingTypeException extends RuntimeException {
    public UnknownRatingTypeException(String message) {
        super(message);
    }
}
