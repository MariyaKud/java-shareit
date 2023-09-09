package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.practicum.shareit.user.model.User;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "items_comments")
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private final User author;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private final Item item;
    private String text;
    private LocalDateTime created;
}
