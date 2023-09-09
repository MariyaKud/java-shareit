package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class BookingDtoView {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusBooking status;
    private ItemDtoShort item;
    private UserDtoShort booker;

    public boolean isRightState(StateBooking state) {
        final LocalDateTime current = LocalDateTime.now();

        if (state == StateBooking.PAST) {
            return end.isBefore(current);
        } else if (state == StateBooking.FUTURE) {
            return start.isAfter(current);
        } else if (state == StateBooking.CURRENT) {
            return Objects.equals(start, current) || Objects.equals(end, current) ||
                    ((start.isBefore(current)) && end.isAfter(current));
        } else {
            return false;
        }
    }
}
