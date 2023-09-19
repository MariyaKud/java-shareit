package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Item> findByAvailableTrueAndContainingText(String text, Pageable page);

    @EntityGraph("item-comment-graph")
    Page<Item> findByOwnerIdOrderById(Long ownerId, Pageable page);

    @EntityGraph("item-comment-graph")
    @Override
    Optional<Item> findById(Long aLong);

    @Query(value = "SELECT * FROM items " +
                   "WHERE request_id in (:requestIds)",
            nativeQuery = true)
    List<Item> findByRequestIds(Set<Long> requestIds);

    List<Item> findByRequestId(Long requestIds);
}