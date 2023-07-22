package ru.practicum.main.error;

public class EditingErrorException extends RuntimeException {
    public EditingErrorException(String message) {
        super(message);
    }
}