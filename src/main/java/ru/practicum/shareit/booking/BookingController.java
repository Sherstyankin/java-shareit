package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.handler.ErrorResponse;
import ru.practicum.shareit.validation.ValidationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ValidationService validationService;

    @PostMapping
    public ResponseBookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid BookingDto bookingDto) {
        validationService.validateStartAndEnd(bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto changeStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long bookingId) {
        return bookingService.findBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> findAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(required = false,
                                                                   defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> findAllBookingByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(required = false,
                                                                       defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingByOwnerItems(userId, state);
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionFailedException(final ConversionFailedException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }
}
