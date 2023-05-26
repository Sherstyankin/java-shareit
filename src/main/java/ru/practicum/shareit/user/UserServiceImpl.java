package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDaoInMemory;
    private Long generatedId = 0L;

    private Long getGeneratedId() {
        return ++generatedId;
    }

    @Override
    public List<User> findAll() {
        log.info("Получаем список всех пользователей.");
        return userDaoInMemory.findAll();
    }

    @Override
    public User create(User user) {
        checkEmail(user.getEmail());
        user.setId(getGeneratedId());
        if (userDaoInMemory.isUserExist(user.getId())) {
            throw new UserAlreadyExistException("Пользователь c " + user.getId() + " уже существует!");
        }
        log.info("Добавляем следующего пользователя: {}", user);
        return userDaoInMemory.create(user);
    }

    @Override
    public User update(User user, Long userId) {
        if (!userDaoInMemory.isUserExist(userId)) {
            log.warn("Пользователь c {} не существует!", userId);
            throw new UserNotFoundException("Пользователь c ID:" + userId + " не существует!");
        }
        User userToUpdate = userDaoInMemory.findById(userId);
        if (user.getEmail() != null && !Objects.equals(user.getEmail(), userToUpdate.getEmail())) {
            checkEmail(user.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        log.info("Обновляем следующего пользователя: {}", userToUpdate);
        return userDaoInMemory.update(userToUpdate, userId);
    }

    @Override
    public User findById(Long userId) {
        if (!userDaoInMemory.isUserExist(userId)) {
            log.warn("Пользователь c ID:{} не существует!", userId);
            throw new UserNotFoundException("Пользователь c ID:" + userId + " не существует!");
        }
        log.info("Получаем пользователя с ID:{}", userId);
        return userDaoInMemory.findById(userId);
    }

    @Override
    public void delete(Long userId) {
        if (!userDaoInMemory.isUserExist(userId)) {
            log.warn("Пользователь c ID:{} не существует!", userId);
            throw new UserNotFoundException("Пользователь c ID:" + userId + " не существует!");
        }
        log.info("Удаляем пользователя под ID: {}", userId);
        userDaoInMemory.delete(userId);
    }

    private void checkEmail(String email) {
        List<String> emailList = userDaoInMemory.findAll().stream().map(User::getEmail).collect(Collectors.toList());
        if (emailList.contains(email)) {
            throw new EmailAlreadyExistsException("Пользователь с email: " + email + " уже существует");
        }
    }
}
