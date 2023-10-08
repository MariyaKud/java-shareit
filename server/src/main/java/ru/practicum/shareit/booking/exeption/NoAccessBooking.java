package ru.practicum.shareit.booking.exeption;

public class NoAccessBooking extends RuntimeException {

    public NoAccessBooking(Long ownerId, Long bookingId) {
        super(String.format("Попытка получить сведение пользователем с id: %s, " +
                             "по бронированию с id: %s", ownerId, bookingId));
    }
}
