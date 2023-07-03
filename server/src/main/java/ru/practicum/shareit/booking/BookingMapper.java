package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
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

@UtilityClass
public class BookingMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public Booking mapToBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(item)
                .booker(user)
                .build();
    }

    public ResponseBookingDto mapToResponseBookingDto(Booking booking) {
        return booking == null ? null : ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(modelMapper.map(booking.getItem(), ItemDto.class))
                .booker(modelMapper.map(booking.getBooker(), UserDto.class))
                .status(booking.getStatus())
                .build();
    }

    public BookingForItemDto mapToBookingForItemDto(Booking booking) {
        return booking == null ? null : BookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public List<ResponseBookingDto> mapToResponseBookingDto(Iterable<Booking> bookings) {
        List<ResponseBookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(mapToResponseBookingDto(booking));
        }
        return dtos;
    }
}
