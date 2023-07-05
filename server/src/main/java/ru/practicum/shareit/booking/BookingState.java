package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, // все категории бронирования
    CURRENT, // текущее бронирование
    PAST, // прошедшее бронирование
    FUTURE, // будущее бронирование (подтвержденное)
    WAITING, // новое бронирование, ожидает одобрения
    REJECTED, // бронирование отклонено владельцем
}
