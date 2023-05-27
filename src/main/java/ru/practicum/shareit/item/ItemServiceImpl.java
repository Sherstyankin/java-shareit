package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public List<Item> findAllUserItems(Long userId) {
        log.info("Получаем все вещи пользователя с ID:{}", userId);
        return itemDao.findAllUserItems(userId);
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Получаем вещь с ID:{}", itemId);
        return itemDao.findById(itemId);
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isBlank()) {
            log.info("Возвращаем пустой список, так как текст запроса не указан.");
            return Collections.emptyList();
        } else {
            log.info("Возвращаем список вещей, который соответствует тексту запроса: '{}'", text);
            return itemDao.findByText(text);
        }
    }

    @Override
    public Item create(Long userId, Item item) {
        checkUser(userId);
        item.setOwner(userDao.findById(userId));
        log.info("Добавляем новую вещь: {}", item);
        return itemDao.create(userId, item);
    }

    @Override
    public Item update(Long userId, Item item, Long itemId) {
        checkUser(userId);
        Item itemToUpdate = itemDao.findById(itemId);
        checkOwner(userId, itemToUpdate.getOwner().getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            log.info("Редактируем название вещи.");
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            log.info("Редактируем описание вещи.");
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            log.info("Изменяем доступность вещи.");
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    private void checkUser(Long userId) {
        if (!userDao.isUserExist(userId)) {
            log.warn("Пользователь c {} не существует!", userId);
            throw new EntityNotFoundException("Пользователь c ID:" + userId + " не существует!", User.class);
        }
    }

    private void checkOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId)) {
            log.warn("Пользователь c ID={} не является владельцем вещи!", userId);
            throw new UserNotOwnerException("Пользователь c ID:" + userId + " не является владельцем вещи!");
        }
    }
}
