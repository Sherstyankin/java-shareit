package ru.practicum.shareit.user;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User findById(Long userId);

    User create(User user);

    User update(User userToUpdate, Long userId);

    void delete(Long userId);

    boolean isUserExist(Long userId);
}
