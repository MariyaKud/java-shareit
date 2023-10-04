package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut createItemRequest(long userId, ItemRequestDto itemRequestDto, LocalDateTime current);

    List<ItemRequestDtoWithItems> getMyItemRequests(long userId);

    List<ItemRequestDtoWithItems> getAllItemRequests(long userId, int from, int size);

    ItemRequestDtoWithItems getRequestById(Long userId, Long requestId);
}
