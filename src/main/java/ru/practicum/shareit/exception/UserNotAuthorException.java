package ru.practicum.shareit.exception;

public class UserNotAuthorException extends RuntimeException {
    public UserNotAuthorException(String s) {
        super(s);
    }
}
