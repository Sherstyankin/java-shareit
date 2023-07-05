package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL, // все категории бронирования
    CURRENT, // текущее бронирование
    PAST, // прошедшее бронирование
    FUTURE, // будущее бронирование (подтвержденное)
    WAITING, // новое бронирование, ожидает одобрения
    REJECTED; // бронирование отклонено владельцем

    public static BookingState from(String stringState) {
        try {
            return BookingState.valueOf(stringState.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + stringState);
        }
    }
}
