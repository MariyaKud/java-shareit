package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDtoView createBooking(Long ownerId, BookingDto bookingDto, LocalDateTime current);

    BookingDtoView approvedBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDtoView getBookingById(Long ownerId, Long bookingId);

    List<BookingDtoView> getBookingsByUserId(Long ownerId, StateBooking stateBooking);

    List<BookingDtoView> getBookingsForItemsByUserId(Long ownerId, StateBooking stateBooking);
}
