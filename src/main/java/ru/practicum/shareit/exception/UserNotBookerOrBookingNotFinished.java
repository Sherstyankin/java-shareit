package ru.practicum.shareit.exception;

public class UserNotBookerOrBookingNotFinished extends RuntimeException {
    public UserNotBookerOrBookingNotFinished(String s) {
        super(s);
    }
}
