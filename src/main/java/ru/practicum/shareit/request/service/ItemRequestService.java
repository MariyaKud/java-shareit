package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.itemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut createItemRequest(long userId, ItemRequestDto itemRequestDto, LocalDateTime current);

    List<itemRequestDtoWithItems> getMyItemRequests(long userId);

    List<itemRequestDtoWithItems> getAllItemRequests(long userId, int from, int size);

    itemRequestDtoWithItems getRequestById(Long userId, Long requestId);
}
