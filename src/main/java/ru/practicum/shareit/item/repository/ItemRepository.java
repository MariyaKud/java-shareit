package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Long getId();

    Item save(Item item);

    List<Item> findItemsByUserId(long userId);

    List<Item> findItemsByUserId(long userId, String text);

    Item findItemByUserId(long itemId);
}
