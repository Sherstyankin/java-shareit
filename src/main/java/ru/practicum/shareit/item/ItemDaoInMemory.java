package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemory implements ItemDao {

    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return new ArrayList<>(items.get(userId).values());
    }

    @Override
    public Item findById(Long itemId) {
        return items.values().stream()
                .filter(entry -> entry.containsKey(itemId))
                .map(entry -> entry.get(itemId))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID:" + itemId + " не найдена."));
    }

    @Override
    public List<Item> findByText(String text) {
        String textWithLowerCase = text.toLowerCase();
        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(item -> (item.getName().toLowerCase().contains(textWithLowerCase)
                        || item.getDescription().toLowerCase().contains(textWithLowerCase))
                        && item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Long userId, Item item) {
        Map<Long, Item> temp = items.getOrDefault(userId, new HashMap<>());
        temp.put(item.getId(), item);
        items.put(userId, temp);
        return item;
    }

    @Override
    public Item update(Long userId, Item itemToUpdate, Long itemId) {
        Map<Long, Item> temp = items.getOrDefault(userId, new HashMap<>());
        temp.put(itemId, itemToUpdate);
        items.put(userId, temp);
        return itemToUpdate;
    }
}
