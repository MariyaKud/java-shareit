package ru.practicum.shareit.item.exeption;

public class ItemUnavailable extends RuntimeException {

    public ItemUnavailable(Long itemId) {
        super(String.format("Вещь с id: %s не доступна для бронирования", itemId));
    }
}
