package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class BookingDtoOutShort {
    private Long id;
    private final Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
