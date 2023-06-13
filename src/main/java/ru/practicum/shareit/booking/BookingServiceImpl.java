package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ResponseBookingDto create(Long userId, BookingDto bookingDto) {
        User booker = findUser(userId);
        Item item = findItem(bookingDto.getItemId());
        checkIsAvailable(item);
        checkIsOwner(userId, item.getOwner().getId());
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);
        return BookingMapper.mapToResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto changeStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = findBooking(bookingId);
        checkIsApproverIsOwner(userId, booking.getItem().getOwner().getId()); // проверить, что пользователь - владелец вещи
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
        Booking booking = findBooking(bookingId);
        checkIsOwnerOrBooker(userId,
                booking.getItem().getOwner().getId(),
                booking.getBooker().getId());
        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> findAllBookingByUserId(Long userId, BookingState state) {
        checkIsUserExists(userId); // проверить существует ли пользователь
        List<Booking> bookingList;
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingByUserId(userId,
                        Sort.by(DESC, "start"));
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingByUserId(userId,
                        Sort.by(DESC, "start"));
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingByUserId(userId,
                        Sort.by(DESC, "start"));
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingByUserId(userId,
                        Sort.by(DESC, "start"));
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingByUserId(userId,
                        Sort.by(DESC, "start"));
                break;
            default:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }
        return bookingList != null ?
                BookingMapper.mapToResponseBookingDto(bookingList) : Collections.emptyList();
    }

    @Override
    public List<ResponseBookingDto> findAllBookingByOwnerItems(Long userId, BookingState state) {
        checkIsUserExists(userId); // проверить существует ли пользователь
        List<Booking> bookingList;
        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
            default:
                bookingList = bookingRepository.findAllBookingByOwnerItems(userId,
                        Sort.by(DESC, "start"));
                break;
        }
        return bookingList != null ?
                BookingMapper.mapToResponseBookingDto(bookingList) : Collections.emptyList();
    }

    private void checkIsOwnerOrBooker(Long userId, Long ownerId, Long bookerId) {
        if (!Objects.equals(userId, ownerId) && !Objects.equals(userId, bookerId)) {
            log.warn("Пользователь c ID={} не является " +
                    "владельцем вещи или автором бронирования!", userId);
            throw new UserNotOwnerOrBookerException("Пользователь c ID:" + userId +
                    " не является владельцем вещи или автором бронирования!");
        }
    }

    private void checkIsApproverIsOwner(Long approverId, Long ownerId) {
        if (!Objects.equals(approverId, ownerId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    approverId + " не является владельцем!", User.class);
        }
    }

    private void checkIsAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с ID: " + item.getId() +
                    " недоступна для бронирования.");
        }
    }

    private void checkIsUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    userId + " не существует!", User.class);
        }
    }

    private void checkIsOwner(Long userId, Long ownerId) {
        if (Objects.equals(userId, ownerId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    userId + " является владельцем!", User.class);
        }
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь c ID:" +
                        itemId + " не существует!", Item.class));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь c ID:" +
                        userId + " не существует!", User.class));
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Бронирование c ID:" +
                                bookingId + " не существует!", Booking.class));
    }
}
