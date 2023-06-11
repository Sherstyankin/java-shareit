package ru.practicum.shareit.exception;

public class InvalidDateTimeException extends RuntimeException {
    public InvalidDateTimeException(String s) {
        super(s);
    }
}
