package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {
    ResponseBookingDto create(Long userId, BookingDto booking);

    ResponseBookingDto changeStatus(Long userId, Long bookingId, Boolean approved);

    ResponseBookingDto findBookingInfo(Long userId, Long bookingId);

    List<ResponseBookingDto> findAllBookingByUserId(Long userId, BookingState state, Integer from,
                                                    Integer size);

    List<ResponseBookingDto> findAllBookingByOwnerItems(Long userId, BookingState state, Integer from,
                                                        Integer size);
}
