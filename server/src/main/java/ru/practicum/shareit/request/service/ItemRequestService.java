package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithItems;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut createItemRequest(long userId, ItemRequestDtoIn itemRequestDto, LocalDateTime current);

    List<ItemRequestDtoOutWithItems> getMyItemRequests(long userId);

    List<ItemRequestDtoOutWithItems> getAllItemRequests(long userId, int from, int size);

    ItemRequestDtoOutWithItems getRequestById(Long userId, Long requestId);
}
