package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemDto getItemByIdForUserId(long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> searchItemsByUserId(long userId, String text);
}
