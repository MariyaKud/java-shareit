package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "items")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
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
