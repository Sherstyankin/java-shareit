package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> findAll() {
        log.info("Получаем список всех пользователей.");
        return repository.findAll();
    }

    @Override
    public User create(User user) {
        log.info("Добавляем следующего пользователя: {}", user);
        return repository.save(user);
    }

    @Override
    public User update(User user, Long userId) {
        User userToUpdate = findById(userId);
        if (user.getEmail() != null
                && !user.getEmail().isEmpty()
                && !Objects.equals(user.getEmail(), userToUpdate.getEmail())) {
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null
                && !user.getName().isEmpty()) {
            userToUpdate.setName(user.getName());
        }
        log.info("Обновляем следующего пользователя: {}", userToUpdate);
        return repository.save(userToUpdate);
    }

    @Override
    public User findById(Long userId) {
        log.info("Получаем пользователя с ID:{}", userId);
        return repository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь c ID:" +
                                userId + " не существует!", User.class));
    }

    @Override
    public void delete(Long userId) {
        User userTodelete = findById(userId);
        log.info("Удаляем пользователя под ID: {}", userId);
        repository.delete(userTodelete);
    }
}
