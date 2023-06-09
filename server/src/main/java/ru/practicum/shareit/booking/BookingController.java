package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос на бронирование от пользователя с ID:{}", userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        log.info("Получен запрос на изменение статуса бронирования с ID:{} от пользователя с ID:{}",
                bookingId,
                userId);
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long bookingId) {
        log.info("Получен запрос на поиск информации по бронированию с ID:{} от пользователя с ID:{}",
                bookingId,
                userId);
        return bookingService.findBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> findAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam BookingState state,
                                                           @RequestParam Integer from,
                                                           @RequestParam Integer size) {
        log.info("Получен запрос на поиск бронирований пользователя с ID:{} по категории {}",
                userId,
                state);
        return bookingService.findAllBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> findAllBookingByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam BookingState state,
                                                               @RequestParam Integer from,
                                                               @RequestParam Integer size) {
        log.info("Получен запрос на поиск бронирований всех вещей владельца с ID:{} по категории {}",
                userId,
                state);
        return bookingService.findAllBookingByOwnerItems(userId, state, from, size);
    }
}
