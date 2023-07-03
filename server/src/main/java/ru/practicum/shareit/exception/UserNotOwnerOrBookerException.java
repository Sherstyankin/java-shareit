package ru.practicum.shareit.exception;

public class UserNotOwnerOrBookerException extends RuntimeException {
    public UserNotOwnerOrBookerException(String s) {
        super(s);
    }
}
