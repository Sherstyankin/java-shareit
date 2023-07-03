package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL, // все категории бронирования
    CURRENT, // текущее бронирование
    PAST, // прошедшее бронирование
    FUTURE, // будущее бронирование (подтвержденное)
    WAITING, // новое бронирование, ожидает одобрения
    REJECTED; // бронирование отклонено владельцем

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
