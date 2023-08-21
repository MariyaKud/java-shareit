package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long id = 0L;
    private final Map<Long, List<Item>> items = new HashMap<>();

    private final Map<Long, Item> allItems = new HashMap<>();

    @Override
    public Long getId() {
        return id + 1;
    }

    @Override
    public Item save(Item item) {
        items.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.removeIf(i -> i.getId().equals(item.getId()));
            userItems.add(item);
            return userItems;
        });

        if (id < item.getId()) {
            id = item.getId();
        }
        allItems.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item findItemByUserId(long itemId) {
        return allItems.get(itemId);
    }

    @Override
    public List<Item> findItemsByUserId(long userId, String text) {

        if (text.isBlank()) {
            return new ArrayList<Item>();

        }

        return allItems.values().stream().filter(Item::getAvailable).filter(f -> f.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }
}
