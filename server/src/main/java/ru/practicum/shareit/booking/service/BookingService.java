package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDtoOut createBooking(Long ownerId, BookingDtoIn bookingDto, LocalDateTime current);

    BookingDtoOut approvedBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDtoOut getBookingById(Long ownerId, Long bookingId);

    List<BookingDtoOut> getBookingsByBookerId(Long bookerId, StateBooking stateBooking, int from, int size);

    List<BookingDtoOut> getBookingsByOwnerId(Long ownerId, StateBooking stateBooking, int from, int size);
}
