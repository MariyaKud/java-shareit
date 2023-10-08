package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequestDtoOut toDto(ItemRequest item) {
        return new ItemRequestDtoOut(item.getId(), item.getDescription(), item.getCreated());
    }

    public ItemRequest fromDto(User author, ItemRequestDtoIn itemRequestDto, LocalDateTime created) {
        return ItemRequest.builder()
                .author(author)
                .description(itemRequestDto.getDescription())
                .created(created)
                .build();
    }

    public ItemRequestDtoOutWithItems toDtoWithItems(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestDtoOutWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
