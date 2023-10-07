package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class BookingDtoIn {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
