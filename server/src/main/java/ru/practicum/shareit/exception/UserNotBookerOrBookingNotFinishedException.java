package ru.practicum.shareit.exception;

public class UserNotBookerOrBookingNotFinishedException extends RuntimeException {
    public UserNotBookerOrBookingNotFinishedException(String s) {
        super(s);
    }
}
