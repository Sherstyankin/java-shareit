package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private final UserRepository mockRepository = Mockito.mock(UserRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final UserService userService = new UserServiceImpl(mockRepository, modelMapper);
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .email("sher@mail.com")
                .name("Сергей")
                .build();
        user = User.builder()
                .id(1L)
                .email("sher@mail.com")
                .name("Сергей")
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(mockRepository.save(user))
                .thenReturn(user);

        UserDto result = userService.create(userDto);
        Assertions.assertEquals(userDto, result);
    }

    @Test
    void update() {
        Mockito
                .when(mockRepository.save(user))
                .thenReturn(user);
        Mockito
                .when(mockRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(userDto, 1L);
        Assertions.assertEquals(userDto, updatedUser);
    }

    @Test
    void findById() {
        Mockito
                .when(mockRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);
        Assertions.assertEquals(userDto, result);
    }

    @Test
    void findNotExistedUser() {
        Mockito
                .when(mockRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
        verify(mockRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser() {
        Mockito
                .when(mockRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.delete(1L);
        verify(mockRepository, times(1)).delete(user);
    }

    @Test
    void deleteNotExistedUser() {
        Mockito
                .when(mockRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));
        verify(mockRepository, never()).delete(user);
    }

    @Test
    void findAll() {
        Mockito
                .when(mockRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> result = userService.findAll();
        Assertions.assertEquals(List.of(userDto), result);
    }
}