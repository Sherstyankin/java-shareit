package ru.practicum.shareit.booking;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.List;

public final class BookingMapper {
    private BookingMapper() {
    }

    private static final ModelMapper modelMapper = new ModelMapper();

    public static Booking mapToBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(item)
                .booker(user)
                .build();
    }

    public static ResponseBookingDto mapToResponseBookingDto(Booking booking) {
        return booking == null ? null : ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(modelMapper.map(booking.getItem(), ItemDto.class))
                .booker(modelMapper.map(booking.getBooker(), UserDto.class))
                .status(booking.getStatus())
                .build();
    }

    public static BookingForItemDto mapToBookingForItemDto(Booking booking) {
        return booking == null ? null : BookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static List<ResponseBookingDto> mapToResponseBookingDto(Iterable<Booking> bookings) {
        List<ResponseBookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapToResponseBookingDto(booking));
        }
        return dtos;
    }
}
