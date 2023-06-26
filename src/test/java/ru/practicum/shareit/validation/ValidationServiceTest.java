package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

class ValidationServiceTest {
    private final ValidationService service = new ValidationService();
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .start(LocalDateTime.of(2023, 6, 29, 12, 23, 23))
                .end(LocalDateTime.of(2023, 6, 28, 12, 23, 23))
                .itemId(1L)
                .build();
    }

    @Test
    void validateStartAndEnd_whenStartOrEndNotValid_thenValidationException() {
        Assertions.assertThrows(ValidationException.class, () -> service.validateStartAndEnd(bookingDto));
    }
}