package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.validation.ValidationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;
    private final ValidationService validationService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto) {
        validationService.validateStartAndEnd(requestDto);
        log.info("Получен запрос на бронирование от пользователя с ID:{}", userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved) {
        log.info("Получен запрос на изменение статуса бронирования с ID:{} от пользователя с ID:{}",
                bookingId,
                userId);
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Получен запрос на поиск информации по бронированию с ID:{} от пользователя с ID:{}",
                bookingId,
                userId);
        return bookingClient.findBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получен запрос на поиск бронирований пользователя с ID:{} по категории {}",
                userId,
                state);
        return bookingClient.findAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получен запрос на поиск бронирований всех вещей владельца с ID:{} по категории {}",
                userId,
                state);
        return bookingClient.findAllBookingByOwnerItems(userId, state, from, size);
    }
}