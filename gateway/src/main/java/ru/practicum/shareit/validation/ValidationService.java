package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationService {
    public void validateStartAndEnd(BookItemRequestDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                Objects.equals(bookingDto.getEnd(), bookingDto.getStart())) {
            log.warn("Время старта/окончания бронирования указано некорректно.");
            throw new ValidationException("Время старта/окончания бронирования указано некорректно.");
        }
    }
}

