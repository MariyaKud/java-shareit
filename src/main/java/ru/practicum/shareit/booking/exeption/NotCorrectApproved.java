package ru.practicum.shareit.booking.exeption;

public class NotCorrectApproved extends RuntimeException {

    public NotCorrectApproved(Long bookingId) {
        super(String.format("Для брони с id: %s уже установлен статус подтвержден, изменить статус нельзя", bookingId));
    }
}
