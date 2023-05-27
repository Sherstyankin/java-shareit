package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemory implements ItemDao {
    private final Map<Long, Item> rawItems = new HashMap<>();
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return new ArrayList<>(items.getOrDefault(userId, null).values());
    }

    @Override
    public Item findById(Long itemId) {
        return rawItems.getOrDefault(itemId, null);
    }

    @Override
    public List<Item> findByText(String text) {
        String textWithLowerCase = text.toLowerCase();
        return rawItems.values().stream()
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
        rawItems.put(item.getId(), item);
        return item;
    }
}
