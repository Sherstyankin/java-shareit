package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    void delete(Long userId);
}
