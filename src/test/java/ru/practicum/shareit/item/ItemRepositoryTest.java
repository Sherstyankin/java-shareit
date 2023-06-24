package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.ASC;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User owner;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .email("sher@mail.com")
                .name("Сергей")
                .build();

        userRepository.save(owner);

        itemRequest = ItemRequest.builder()
                .description("Нужен рюкзак")
                .requestor(owner)
                .build();

        itemRequestRepository.save(itemRequest);

        item = Item.builder()
                .owner(owner)
                .itemRequest(itemRequest)
                .available(true)
                .name("Рюкзак")
                .description("Походный рюкзак")
                .build();

        itemRepository.save(item);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByOwnerIdOrderByIdAsc_whenInvoked_then_ResponseContainsListWithItem() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Item> itemList = itemRepository.findByOwnerIdOrderByIdAsc(1L, pageable);

        assertEquals(1, itemList.size());
        assertEquals("Походный рюкзак", itemList.get(0).getDescription());
    }

    @Test
    void findByText_whenInvoked_then_ResponseContainsListWithItem() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Item> itemList = itemRepository.findByText("РюК", pageable);

        assertEquals(1, itemList.size());
        assertEquals("Походный рюкзак", itemList.get(0).getDescription());
    }

    @Test
    void findByItemRequestIn_whenInvoked_then_ResponseContainsListWithItem() {
        Sort sort = Sort.by(ASC, "id");
        List<Item> itemList = itemRepository.findByItemRequestIn(List.of(itemRequest), sort);

        assertEquals(1, itemList.size());
        assertEquals("Походный рюкзак", itemList.get(0).getDescription());
    }

    @Test
    void findByItemRequest_whenInvoked_then_ResponseContainsListWithItem() {
        Sort sort = Sort.by(ASC, "id");
        List<Item> itemList = itemRepository.findByItemRequest(itemRequest, sort);

        assertEquals(1, itemList.size());
        assertEquals("Походный рюкзак", itemList.get(0).getDescription());
    }
}