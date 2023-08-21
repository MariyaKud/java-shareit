package ru.practicum.shareit.item.exeption;

public class ItemNotFoundById extends RuntimeException {
    public ItemNotFoundById(Long id) {
        super(String.format("Item с id = %s не найден", id));
    }
}
