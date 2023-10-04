package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemDtoWithBookings getItemByIdForUserId(long userId, long itemId);

    List<ItemDtoWithBookings> getItemsByUserId(long userId, int from, int size);

    List<ItemDto> searchItemsForUserWithId(long userId, String text, int from, int size);

    CommentDto createComment(long userId, long itemId, CommentDtoShort commentDto, LocalDateTime current);
}
