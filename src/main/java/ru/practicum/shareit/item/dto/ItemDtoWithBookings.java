package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.Set;

@Getter
@Setter
@Builder
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private Set<CommentDto> comments;
}
