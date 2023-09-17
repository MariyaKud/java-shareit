package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items " +
                   "WHERE available = true " +
                   "and (LOWER(name) LIKE LOWER(CONCAT('%', :text,'%')) " +
                   "or LOWER(description) LIKE LOWER(CONCAT('%', :text,'%')))",
            nativeQuery = true)
    List<Item> findByAvailableTrueAndContainingText(String text);

    @EntityGraph("item-comment-graph")
    List<Item> findByOwnerIdOrderById(Long ownerId);

    @EntityGraph("item-comment-graph")
    @Override
    Optional<Item> findById(Long aLong);

    @Query(value = "SELECT * FROM items " +
                   "WHERE request_id in (:requestIds)",
            nativeQuery = true)
    List<Item> findByRequestIds(Set<Long> requestIds);

    List<Item> findByRequestId(Long requestIds);
}