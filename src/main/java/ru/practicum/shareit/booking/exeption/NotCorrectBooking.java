package ru.practicum.shareit.booking.exeption;
import java.time.LocalDateTime;

public class NotCorrectBooking extends RuntimeException {

    public NotCorrectBooking(LocalDateTime start, LocalDateTime end) {
        super(String.format("Не корректно указан период бронирования - start: %s, end: %s", start, end));
    }
}
