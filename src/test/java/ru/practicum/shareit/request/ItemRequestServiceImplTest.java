package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.Direction.ASC;

class ItemRequestServiceImplTest {
    private final ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    private final ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    private final UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final ItemRequestService itemRequestService = new ItemRequestServiceImpl(
            mockItemRequestRepository,
            mockItemRepository,
            mockUserRepository,
            modelMapper);
    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemRequestResponseDto itemRequestResponseForCreateMethodDto;
    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private ItemForItemRequestDto itemForItemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("sher@mail.com")
                .name("Сергей")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужна палатка")
                .created(LocalDateTime.of(2023, 6, 30, 12, 23))
                .requestor(user)
                .build();

        item = Item.builder()
                .id(1L)
                .itemRequest(itemRequest)
                .owner(user)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .build();

        itemForItemRequestDto = ItemForItemRequestDto.builder()
                .id(1L)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .requestId(1L)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Нужна палатка")
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 6, 30, 12, 23))
                .description("Нужна палатка")
                .items(List.of(itemForItemRequestDto))
                .build();

        itemRequestResponseForCreateMethodDto = ItemRequestResponseDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 6, 30, 12, 23))
                .description("Нужна палатка")
                .build();
    }

    @Test
    void create() {
        ArgumentCaptor<ItemRequest> argumentCaptor = ArgumentCaptor.forClass(ItemRequest.class);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRequestRepository.save(argumentCaptor.capture()))
                .thenReturn(itemRequest);

        ItemRequestResponseDto result = itemRequestService.create(1L, itemRequestDto);
        Assertions.assertEquals(itemRequestResponseForCreateMethodDto, result);
    }

    @Test
    void findAllRequestsByRequestor() {
        Mockito
                .when(mockUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(mockItemRepository
                        .findByItemRequestIn(List.of(itemRequest), Sort.by(ASC, "id")))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.findAllRequestsByRequestor(1L);
        Assertions.assertEquals(List.of(itemRequestResponseDto), result);
    }

    @Test
    void findAllRequestsWithoutPagination() {
        Mockito
                .when(mockUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(mockItemRepository
                        .findByItemRequestIn(List.of(itemRequest), Sort.by(ASC, "id")))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.findAllRequests(1L, null,null);
        Assertions.assertEquals(List.of(itemRequestResponseDto), result);
    }

    @Test
    void findAllRequestsWithPagination() {
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));

        Mockito
                .when(mockUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestRepository
                        .findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        Mockito
                .when(mockItemRepository
                        .findByItemRequestIn(List.of(itemRequest), Sort.by(ASC, "id")))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.findAllRequests(1L, 0,1);
        Assertions.assertEquals(List.of(itemRequestResponseDto), result);

    }

    @Test
    void findRequestByIdWhenRequestIsExisted() {
        Mockito
                .when(mockUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(mockItemRepository.findByItemRequest(itemRequest, Sort.by(ASC, "id")))
                .thenReturn(List.of(item));
        ItemRequestResponseDto result = itemRequestService.findRequestById(1L,1L);
        Assertions.assertEquals(itemRequestResponseDto, result);
    }

    @Test
    void findRequestByIdWhenRequestIsNotExisted() {
        Mockito
                .when(mockUserRepository.existsById(user.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestRepository.findById(1L))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> itemRequestService
                .findRequestById(1L,1L));
    }
}