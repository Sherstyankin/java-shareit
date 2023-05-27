package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("UserDaoInMemory")
public class UserDaoInMemory implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 0L;

    private Long getGeneratedId() {
        return ++generatedId;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        user.setId(getGeneratedId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return users.containsKey(userId);
    }
}
