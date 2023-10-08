package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingDtoOut toDto(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(itemMapper.toShortDto(booking.getItem()))
                .booker(userMapper.toDto(booking.getBooker()))
                .build();
    }

    public Booking fromDto(BookingDtoIn bookingDto, User booker, Item item, StatusBooking statusBooking) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(item)
                .booker(booker)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(statusBooking)
                .build();
    }

    public List<BookingDtoOut> bookingsToDto(Page<Booking> bookings) {
        return  bookings.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
    }
}
