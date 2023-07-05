package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Booking waitingBooking;
    private Booking rejectedBooking;
    private Booking currentBooking;
    private Booking futureBooking;
    private Booking pastBooking;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("sher@mail.com")
                .name("Сергей")
                .build();

        userRepository.save(user);

        itemRequest = ItemRequest.builder()
                .description("Нужен рюкзак")
                .requestor(user)
                .build();

        itemRequestRepository.save(itemRequest);

        item = Item.builder()
                .owner(user)
                .itemRequest(itemRequest)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .build();

        itemRepository.save(item);

        currentBooking = Booking.builder()
                .item(item)
                .booker(user)
                .end(LocalDateTime.of(2024, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 23, 12, 23))
                .status(BookingStatus.APPROVED)
                .build();

        bookingRepository.save(currentBooking);

        pastBooking = Booking.builder()
                .item(item)
                .booker(user)
                .end(LocalDateTime.of(2023, 6, 23, 12, 23))
                .start(LocalDateTime.of(2023, 6, 21, 12, 23))
                .status(BookingStatus.REJECTED)
                .build();

        bookingRepository.save(pastBooking);

        futureBooking = Booking.builder()
                .item(item)
                .booker(user)
                .end(LocalDateTime.of(2024, 6, 30, 12, 23))
                .start(LocalDateTime.of(2024, 6, 29, 12, 23))
                .status(BookingStatus.WAITING)
                .build();

        bookingRepository.save(futureBooking);

        pageable = PageRequest.of(0, 5, Sort.by(DESC, "start"));
    }

    @Test
    void findByBookerId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findByBookerId(user.getId(), pageable);

        assertEquals(3, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findCurrentBookingByUserId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findCurrentBookingByUserId(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void findPastBookingByUserId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findPastBookingByUserId(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 21, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void findFutureBookingByUserId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findFutureBookingByUserId(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findWaitingBookingByUserId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findWaitingBookingByUserId(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findRejectedBookingByUserId_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findRejectedBookingByUserId(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 21, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void findAllBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findAllBookingByOwnerItems(user.getId(), pageable);

        assertEquals(3, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findCurrentBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findCurrentBookingByOwnerItems(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void findPastBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findPastBookingByOwnerItems(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 21, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void findFutureBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findFutureBookingByOwnerItems(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findWaitingBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findWaitingBookingByOwnerItems(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findRejectedBookingByOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findRejectedBookingByOwnerItems(user.getId(), pageable);

        assertEquals(1, bookingList.size());
        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 21, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.REJECTED, bookingList.get(0).getStatus());
    }

    @Test
    void findLastBookingForItem_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findLastBookingForItem(item.getId());

        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void findNextBookingForItem_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findNextBookingForItem(item.getId());

        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void findLastBookingForAllOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findLastBookingForItem(item.getId());

        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2023, 6, 23, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
    }

    @Test
    void findNextBookingForAllOwnerItems_whenInvoked_then_ResponseContainsListWithBooking() {
        List<Booking> bookingList = bookingRepository.findNextBookingForItem(item.getId());

        assertEquals("Походный рюкзак", bookingList.get(0).getItem().getDescription());
        assertEquals("Сергей", bookingList.get(0).getBooker().getName());
        assertEquals(LocalDateTime.of(2024, 6, 29, 12, 23),
                bookingList.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 6, 30, 12, 23),
                bookingList.get(0).getEnd());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void checkIsBookerAndFinished_whenInvoked_then_ResponseIsTrue() {
        Boolean isTrue = bookingRepository.checkIsBookerAndFinished(user.getId(), item.getId());

        assertTrue(isTrue);
    }

    @Test
    void checkIsBookerAndFinished_whenInvokedByNotBooker_then_ResponseIsFalse() {
        Boolean isTrue = bookingRepository.checkIsBookerAndFinished(2L, 1L);

        assertFalse(isTrue);
    }


}