package ru.practicum.shareit.user;

import java.util.List;
@Deprecated(since = "Приложение теперь работает с базой данных.")
public interface UserDao {
    List<User> findAll();

    User findById(Long userId);

    User create(User user);

    void delete(Long userId);

    boolean isUserExist(Long userId);
}
