package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class ItemServiceImplTest {
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
    private final ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ItemService itemService = new ItemServiceImpl(
            mockItemRepository,
            mockUserRepository,
            mockBookingRepository,
            mockCommentRepository,
            mockItemRequestRepository,
            modelMapper);
    private ResponseItemDto responseItemDto;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private User user;
    private Booking booking;
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
    void findAllOwnerItems() {
        Mockito
                .when(mockItemRepository
                        .findByOwnerIdOrderByIdAsc(1L))
                .thenReturn(List.of(item));
        Mockito
                .when(mockItemRepository
                        .findByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(mockCommentRepository.findByItemIn(List.of(item), Sort.by(DESC, "created")))
                        .thenReturn(List.of(comment));
        Mockito
                .when(mockBookingRepository.findNextBookingForAllOwnerItems(1L))
                .thenReturn(List.of(booking));
        Mockito
                .when(mockBookingRepository.findLastBookingForAllOwnerItems(1L))
                .thenReturn(List.of(booking));

        List<ResponseItemDto> result = itemService.findAllOwnerItems(1L, null, null);
        Assertions.assertEquals(List.of(responseItemDto), result);
    }

    @Test
    void findById() {
        Mockito
                .when(mockItemRepository
                        .findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockItemRepository
                        .findByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(mockCommentRepository.findByItemId(1L))
                .thenReturn(List.of(comment));
        Mockito
                .when(mockBookingRepository.findNextBookingForItem(1L))
                .thenReturn(List.of(booking));
        Mockito
                .when(mockBookingRepository.findLastBookingForItem(1L))
                .thenReturn(List.of(booking));

        ResponseItemDto result = itemService.findById(1L, 1L);
        Assertions.assertEquals(responseItemDto, result);
    }

    @Test
    void findByTextWithCorrectMatch() {
        Mockito
                .when(mockItemRepository.findByText("РюК"))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.findByText("РюК", null, null);
        Assertions.assertEquals(List.of(itemDto), result);
    }

    @Test
    void findByTextWithBlankInput() {
        Mockito
                .when(mockItemRepository.findByText(" "))
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.findByText(" ", null, null);
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    void create() {
        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(item))
                .thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);
        Assertions.assertEquals(itemDto, result);
    }

    @Test
    void update() {
        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockItemRepository.save(item))
                .thenReturn(item);

        ItemDto result = itemService.update(1L, itemDto,1L);
        Assertions.assertEquals(itemDto, result);
    }

    @Test
    void addComment() {
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockBookingRepository.checkIsBookerAndFinished(1L, 1L))
                .thenReturn(true);
        Mockito
                .when(mockCommentRepository.save(argumentCaptor.capture()))
                .thenReturn(comment);

        ResponseCommentDto result = itemService.addComment(1L, requestCommentDto, 1L);
        Assertions.assertEquals(responseCommentDto, result);
    }
}