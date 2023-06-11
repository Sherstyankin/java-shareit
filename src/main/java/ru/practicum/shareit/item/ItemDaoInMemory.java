package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated(since = "Приложение теперь работает с базой данных.")
@Repository("ItemDaoInMemory")
public class ItemDaoInMemory implements ItemDao {
    private final Map<Long, Item> rawItems = new HashMap<>();
    private final Map<Long, List<Item>> items = new HashMap<>();
    private Long generatedId = 0L;

    private Long getGeneratedId() {
        return ++generatedId;
    }

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return items.get(userId);
    }

    @Override
    public Item findById(Long itemId) {
        return rawItems.get(itemId);
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
        item.setId(getGeneratedId());
        final List<Item> itemsList = items.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        itemsList.add(item);
        rawItems.put(item.getId(), item);
        return item;
    }
}
