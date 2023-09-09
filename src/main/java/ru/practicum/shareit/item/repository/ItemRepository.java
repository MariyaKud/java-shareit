package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items WHERE available = true and " +
                   "(LOWER(name) LIKE LOWER(CONCAT('%', :text,'%')) " +
                   "or LOWER(description) LIKE LOWER(CONCAT('%', :text,'%')))",
            nativeQuery = true)
    List<Item> findByAvailableTrueAndContainingText(String text);

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @EntityGraph("item-comment-graph")
    @Override
    Optional<Item> findById(Long aLong);
}