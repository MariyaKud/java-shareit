package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private Long id;
    private final Long ownerId;
    private String name;
    private String description;
    private Boolean available;

    public boolean isEligibleForSearchText(String text) {
        return getAvailable()
                && (getName().toLowerCase().contains(text)
                 || getDescription().toLowerCase().contains(text));
    }
}
