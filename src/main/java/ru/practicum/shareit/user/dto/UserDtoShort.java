package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDtoShort {
    private final Long id;
    private final String email;
}
