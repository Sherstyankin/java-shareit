package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<Item> findAllUserItems(Long userId);

    Item findById(Long itemId);

    List<Item> findByText(String text);

    Item create(Long userId, Item item);

    Item update(Long userId, Item itemToUpdate, Long itemId);
}
