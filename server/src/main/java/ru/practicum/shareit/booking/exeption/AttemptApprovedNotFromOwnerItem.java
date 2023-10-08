package ru.practicum.shareit.booking.exeption;

public class AttemptApprovedNotFromOwnerItem extends RuntimeException {

    public AttemptApprovedNotFromOwnerItem(Long ownerId, Long itemId) {
        super(String.format("Попытка подтвердить бронирование от пользователя с id: %s, " +
                             "хотя он не владелец бронируемой вещи c id: %s", ownerId, itemId));
    }
}
