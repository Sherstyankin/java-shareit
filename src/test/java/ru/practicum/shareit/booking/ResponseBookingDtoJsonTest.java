package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ResponseBookingDtoJsonTest {
    @Autowired
    private JacksonTester<ResponseBookingDto> json;

    @Test
    void testResponseBookingDto() throws Exception {
        ResponseBookingDto dto = ResponseBookingDto.builder()
                .start(LocalDateTime.of(2023, 6, 29, 12, 23, 23))
                .end(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<ResponseBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathValue("$.start", LocalDateTime.class)
                .isEqualTo(LocalDateTime.of(2023, 6, 29, 12, 23, 23)
                        .toString());
        assertThat(result).extractingJsonPathValue("$.end", LocalDateTime.class)
                .isEqualTo(LocalDateTime.of(2023, 6, 30, 12, 23, 23)
                        .toString());
        assertThat(result).extractingJsonPathValue("$.status", BookingStatus.class)
                .isEqualTo(BookingStatus.WAITING.toString());
    }
}
