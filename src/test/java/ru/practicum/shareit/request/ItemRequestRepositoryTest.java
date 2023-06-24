package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .email("sher@mail.com")
                .name("Сергей")
                .build());

        user2 = userRepository.save(User.builder()
                .email("oleg@mail.com")
                .name("Олег")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Нужна палатка")
                .requestor(user1)
                .build());


        itemRequestRepository.save(ItemRequest.builder()
                .description("Нужна пила")
                .requestor(user2)
                .build());
    }

    @AfterEach
    void clearRepos() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_whenInvoked_thenResponseContainsListWithUser1() {
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(user1.getId());

        assertEquals(1, itemRequestList.size());
        assertEquals("Нужна палатка", itemRequestList.get(0).getDescription());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc_whenInvoked_thenResponseContainsListWithUser2() {
        Pageable pageable = PageRequest.of(0, 5);
        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(user1.getId(), pageable).getContent();

        assertEquals(1, itemRequestList.size());
        assertEquals("Нужна пила", itemRequestList.get(0).getDescription());
    }
}