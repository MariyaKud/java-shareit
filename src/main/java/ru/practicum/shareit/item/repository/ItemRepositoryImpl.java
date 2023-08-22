package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long id = 1L;
    private final Map<Long, List<Item>> items = new HashMap<>();

    private final Map<Long, Item> allItems = new HashMap<>();

    private Long getId() {
        return id++;
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(getId());
        }

        items.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.removeIf(i -> i.getId().equals(item.getId()));
            userItems.add(item);
            return userItems;
        });

        allItems.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> geItemById(long itemId) {
        return Optional.ofNullable(allItems.get(itemId));
    }

    @Override
    public List<Item> findItemsByUserId(long userId, String text) {
        if (text.isBlank()) {
            return List.of();
        }

        final String strForSearch = text.toLowerCase();

        return allItems.values()
                       .stream()
                       .filter(f -> f.isEligibleForSearchText(strForSearch))
                       .collect(Collectors.toList());
    }
}
