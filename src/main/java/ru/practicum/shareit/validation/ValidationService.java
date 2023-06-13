package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.InvalidDateTimeException;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationService {
    public void validateStartAndEnd(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                Objects.equals(bookingDto.getEnd(), bookingDto.getStart())) {
            log.warn("Время старта/окончания бронирования указано некорректно.");
            throw new InvalidDateTimeException("Время старта/окончания бронирования указано некорректно.");
        }
    }
}

