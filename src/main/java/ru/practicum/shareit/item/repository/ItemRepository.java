package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> geItemById(long itemId);

    List<Item> findItemsByUserId(long userId);

    List<Item> findItemsByUserId(long userId, String text);
}
