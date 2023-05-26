package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long userId);

    User create(User user);

    User update(User user, Long userId);

    void delete(Long userId);
}
