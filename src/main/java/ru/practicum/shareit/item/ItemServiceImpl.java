package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnerException;
import ru.practicum.shareit.user.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDaoInMemory;
    private final UserDao userDaoInMemory;
    private Long generatedId = 0L;

    private Long getGeneratedId() {
        return ++generatedId;
    }

    @Override
    public List<Item> findAllUserItems(Long userId) {
        log.info("Получаем все вещи пользователя с ID:{}", userId);
        return itemDaoInMemory.findAllUserItems(userId);
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Получаем вещь с ID:{}", itemId);
        return itemDaoInMemory.findById(itemId);
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isEmpty()) {
            log.info("Возвращаем пустой список, так как текст запроса не указан.");
            return Collections.emptyList();
        } else {
            log.info("Возвращаем список вещей, который соответствует тексту запроса: '{}'", text);
            return itemDaoInMemory.findByText(text);
        }
    }

    @Override
    public Item create(Long userId, Item item) {
        checkUser(userId);
        item.setId(getGeneratedId());
        item.setOwner(userDaoInMemory.findById(userId));
        log.info("Добавляем новую вещь: {}", item);
        return itemDaoInMemory.create(userId, item);
    }

    @Override
    public Item update(Long userId, Item item, Long itemId) {
        checkUser(userId);
        checkOwner(userId, itemId);
        Item itemToUpdate = itemDaoInMemory.findById(itemId);
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        log.info("Редактируем вещь.");
        return itemDaoInMemory.update(userId, itemToUpdate, itemId);
    }

    private void checkUser(Long userId) {
        if (!userDaoInMemory.isUserExist(userId)) {
            log.warn("Пользователь c {} не существует!", userId);
            throw new UserNotFoundException("Пользователь c ID:" + userId + " не существует!");
        }
    }

    private void checkOwner(Long userId, Long itemId) {
        if (!Objects.equals(userId, itemDaoInMemory.findById(itemId).getOwner().getId())) {
            log.warn("Пользователь c ID={} не является владельцем вещи!", userId);
            throw new UserNotOwnerException("Пользователь c ID:" + userId + " не является владельцем вещи!");
        }
    }
}
