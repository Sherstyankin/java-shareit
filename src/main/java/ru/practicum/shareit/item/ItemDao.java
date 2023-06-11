package ru.practicum.shareit.item;

import java.util.List;

@Deprecated(since = "Приложение теперь работает с базой данных.")
public interface ItemDao {
    List<Item> findAllUserItems(Long userId);

    Item findById(Long itemId);

    List<Item> findByText(String text);

    Item create(Long userId, Item item);
}
