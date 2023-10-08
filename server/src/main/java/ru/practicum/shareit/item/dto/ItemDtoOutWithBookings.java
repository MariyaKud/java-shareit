package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.booking.dto.BookingDtoOutShort;

import java.util.Set;

@Getter
@Setter
@Builder
public class ItemDtoOutWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOutShort lastBooking;
    private BookingDtoOutShort nextBooking;
    private Set<CommentDtoOut> comments;
}
