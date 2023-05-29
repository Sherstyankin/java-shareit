package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<User> findAll() {
        log.info("Получаем список всех пользователей.");
        return userDao.findAll();
    }

    @Override
    public User create(User user) {
        checkEmail(user.getEmail());
        log.info("Добавляем следующего пользователя: {}", user);
        return userDao.create(user);
    }

    @Override
    public User update(User user, Long userId) {
        if (!userDao.isUserExist(userId)) {
            log.warn("Пользователь c {} не существует!", userId);
            throw new EntityNotFoundException("Пользователь c ID:" + userId + " не существует!", User.class);
        }
        User userToUpdate = userDao.findById(userId);
        if (user.getEmail() != null
                && !user.getEmail().isEmpty()
                && !Objects.equals(user.getEmail(), userToUpdate.getEmail())) {
            checkEmail(user.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null
                && !user.getName().isEmpty()) {
            userToUpdate.setName(user.getName());
        }
        log.info("Обновляем следующего пользователя: {}", userToUpdate);
        return userToUpdate;
    }

    @Override
    public User findById(Long userId) {
        if (!userDao.isUserExist(userId)) {
            log.warn("Пользователь c ID:{} не существует!", userId);
            throw new EntityNotFoundException("Пользователь c ID:" + userId + " не существует!", User.class);
        }
        log.info("Получаем пользователя с ID:{}", userId);
        return userDao.findById(userId);
    }

    @Override
    public void delete(Long userId) {
        if (!userDao.isUserExist(userId)) {
            log.warn("Пользователь c ID:{} не существует!", userId);
            throw new EntityNotFoundException("Пользователь c ID:" + userId + " не существует!", User.class);
        }
        log.info("Удаляем пользователя под ID: {}", userId);
        userDao.delete(userId);
    }

    private void checkEmail(String email) {
        List<String> emailList = userDao.findAll().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (emailList.contains(email)) {
            throw new EmailAlreadyExistsException("Пользователь с email: " + email + " уже существует");
        }
    }
}
