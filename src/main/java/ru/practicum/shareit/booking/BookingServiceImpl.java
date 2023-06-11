package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ReceivedStatusAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotOwnerOrBookerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ResponseBookingDto create(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Автор бронирования c ID:" +
                                userId + " не существует!", User.class));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Вещь c ID:" +
                                bookingDto.getItemId() + " не существует!", Item.class));

        checkItem(item);
        checkIfOwner(userId, item.getOwner().getId());
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);
        return BookingMapper.mapToResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto changeStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование c ID:" +
                                userId + " не существует!", Booking.class)); // проверка наличия бронирования
        checkIfApproverIsOwner(userId, booking.getItem().getOwner().getId()); // проверить, что пользователь - владелец вещи
        if ((approved && booking.getStatus() == BookingStatus.APPROVED)) { // проверить, что изменение не повторное
            throw new ReceivedStatusAlreadyExistsException("Переданный статус уже установлен.");
        } else if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapToResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto findBookingInfo(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование c ID:" +
                                userId + " не существует!", Booking.class));
        checkIfOwnerOrBooker(userId,
                booking.getItem().getOwner().getId(),
                booking.getBooker().getId());
        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> findAllBookingByUserId(Long userId, BookingState state) {
        checkUser(userId); // проверить существует ли пользователь
        List<Booking> bookingList;
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingByUserId(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingByUserId(userId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingByUserId(userId);
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingByUserId(userId);
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingByUserId(userId);
                break;
            default:
                bookingList = bookingRepository.findAllBookingByUserId(userId);
                break;
        }
        return bookingList != null ?
                BookingMapper.mapToResponseBookingDto(bookingList) : Collections.emptyList();
    }

    @Override
    public List<ResponseBookingDto> findAllBookingByOwnerItems(Long userId, BookingState state) {
        checkUser(userId); // проверить существует ли пользователь
        List<Booking> bookingList;
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingByOwnerItems(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingByOwnerItems(userId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingByOwnerItems(userId);
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingByOwnerItems(userId);
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingByOwnerItems(userId);
                break;
            default:
                bookingList = bookingRepository.findAllBookingByOwnerItems(userId);
                break;
        }
        return bookingList != null ?
                BookingMapper.mapToResponseBookingDto(bookingList) : Collections.emptyList();
    }

    private void checkIfOwnerOrBooker(Long userId, Long ownerId, Long bookerId) {
        if (!Objects.equals(userId, ownerId) && !Objects.equals(userId, bookerId)) {
            log.warn("Пользователь c ID={} не является " +
                    "владельцем вещи или автором бронирования!", userId);
            throw new UserNotOwnerOrBookerException("Пользователь c ID:" + userId +
                    " не является владельцем вещи или автором бронирования!");
        }
    }

    private void checkIfApproverIsOwner(Long approverId, Long ownerId) {
        if (!Objects.equals(approverId, ownerId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    approverId + " не является владельцем!", User.class);
        }
    }

    private void checkItem(Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с ID: " + item.getId() +
                    " недоступна для бронирования.");
        }
    }

    private void checkUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь c ID:" +
                        userId + " не существует!", User.class));
    }

    private void checkIfOwner(Long userId, Long ownerId) {
        if (Objects.equals(userId, ownerId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    userId + " является владельцем!", User.class);
        }
    }
}
