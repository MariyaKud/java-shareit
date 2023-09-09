package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    @Positive(groups = Create.class)
    private Long itemId;
    @NotNull(groups = Create.class)
    private LocalDateTime start;
    @NotNull(groups = Create.class)
    private LocalDateTime end;
    private StatusBooking status;
}
