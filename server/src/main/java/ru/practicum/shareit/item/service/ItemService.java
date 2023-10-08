package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutWithBookings;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemDtoOutWithBookings getItemByIdForUserId(long userId, long itemId);

    List<ItemDtoOutWithBookings> getItemsByUserId(long userId, int from, int size);

    List<ItemDto> searchItemsForUserWithId(long userId, String text, int from, int size);

    CommentDtoOut createComment(long userId, long itemId, CommentDtoIn commentDto, LocalDateTime current);
}
