package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.Direction.DESC;

class BookingServiceImplTest {
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    private final BookingService bookingService = new BookingServiceImpl(
            mockBookingRepository,
            mockUserRepository,
            mockItemRepository);
    private ResponseItemDto responseItemDto;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private User user;
    private UserDto userDto;
    private Booking booking;
    private BookingDto bookingDto;
    private ResponseBookingDto responseBookingDto;
    private ResponseCommentDto responseCommentDto;
    private RequestCommentDto requestCommentDto;
    private BookingForItemDto bookingForItemDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("sher@mail.com")
                .name("Сергей")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("sher@mail.com")
                .name("Сергей")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .build();

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .text("Отличный рюкзак")
                .created(LocalDateTime.of(2023, 6, 30, 12, 23))
                .item(item)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .end(LocalDateTime.of(2023, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 29, 12, 23))
                .status(BookingStatus.WAITING)
                .build();

        bookingForItemDto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .end(LocalDateTime.of(2023, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 29, 12, 23))
                .build();

        bookingDto = BookingDto.builder()
                .end(LocalDateTime.of(2023, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 29, 12, 23))
                .itemId(1L)
                .build();

        responseBookingDto = ResponseBookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .end(LocalDateTime.of(2023, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 29, 12, 23))
                .status(BookingStatus.WAITING)
                .build();

        responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .text("Отличный рюкзак")
                .created(LocalDateTime.of(2023, 6, 30, 12, 23))
                .authorName("Сергей")
                .build();

        requestCommentDto = RequestCommentDto.builder()
                .text("Отличный рюкзак")
                .build();

        responseItemDto = ResponseItemDto.builder()
                .id(1L)
                .available(true)
                .comments(List.of(responseCommentDto))
                .nextBooking(bookingForItemDto)
                .lastBooking(bookingForItemDto)
                .description("Походный рюкзак")
                .name("Рюкзак")
                .build();
    }

    @Test
    void create() {
        ArgumentCaptor<Booking> argumentCaptor = ArgumentCaptor.forClass(Booking.class);

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.save(argumentCaptor.capture()))
                .thenReturn(booking);

        ResponseBookingDto result = bookingService.create(2L, bookingDto);
        Assertions.assertEquals(responseBookingDto, result);
    }

    @Test
    void createBookingByItemOwner() {
        ArgumentCaptor<Booking> argumentCaptor = ArgumentCaptor.forClass(Booking.class);

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.save(argumentCaptor.capture()))
                .thenReturn(booking);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void changeStatus() {
        ResponseBookingDto responseBookingDto2 = ResponseBookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .end(LocalDateTime.of(2023, 6, 30, 12, 23))
                .start(LocalDateTime.of(2023, 6, 29, 12, 23))
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(mockBookingRepository.save(booking))
                .thenReturn(booking);

        ResponseBookingDto result = bookingService.changeStatus(1L, 1L, true);
        Assertions.assertEquals(responseBookingDto2, result);
    }

    @Test
    void findBookingInfo() {
        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        ResponseBookingDto result = bookingService.findBookingInfo(1L, 1L);
        Assertions.assertEquals(responseBookingDto, result);
    }

    @Test
    void findAllBookingByUserId() {
        Mockito
                .when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingRepository
                        .findCurrentBookingByUserId(1L, Sort.by(DESC, "start")))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.findAllBookingByUserId(
                1L,
                BookingState.CURRENT,
                null,
                null);
        Assertions.assertEquals(List.of(responseBookingDto), result);
    }

    @Test
    void findAllBookingByOwnerItems() {
        Mockito
                .when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingRepository
                        .findCurrentBookingByOwnerItems(1L, Sort.by(DESC, "start")))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = bookingService.findAllBookingByOwnerItems(
                1L,
                BookingState.CURRENT,
                null,
                null);
        Assertions.assertEquals(List.of(responseBookingDto), result);
    }
}