package ru.practicum.shareit.item.exeption;

public class ItemBelongsAnotherOwner extends RuntimeException {

    public ItemBelongsAnotherOwner() {
        super("Не корректно указан владелец вещи.");
    }
}
