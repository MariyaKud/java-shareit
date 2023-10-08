package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDtoOut {
    private final Long id;
    private final String name;
}
