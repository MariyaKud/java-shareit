package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemWithBookings getItemByIdForUserId(long userId, long itemId);

    List<ItemWithBookings> getItemsByUserId(long userId);

    List<ItemDto> searchItemsForUserWithId(long userId, String text);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto, LocalDateTime current);
}
