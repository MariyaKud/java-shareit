package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE item_id = :itemId AND user_id = :bookerId AND status = :status AND end_data <= :current",
            nativeQuery = true)
    List<Booking> findByBookerIdAndItemIdAndStatus(Long bookerId, Long itemId, String status, LocalDateTime current);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE item_id = :itemId AND " +
                   "((start_data BETWEEN :start and :end) " +
                   " OR (end_data BETWEEN :start and :end) " +
                   " OR (start_data >= :start and end_data <= :end)) " +
                   "ORDER BY start_data DESC",
            nativeQuery = true)
    List<Booking> findByItemIdAndStartAndEndBetween(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE item_id in (:itemIds) AND status = :status " +
                   "ORDER BY start_data, end_data",
            nativeQuery = true)
    List<Booking> findByItemsBooking(Set<Long> itemIds, String status);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
                   "ORDER BY start_data DESC",
            nativeQuery = true)
    List<Booking> findByOwnerIdItem(Long ownerId);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE item_id in (SELECT i.id FROM items AS i WHERE i.user_id = :ownerId) AND status = :status " +
                   "ORDER BY start_data DESC",
            nativeQuery = true)
    List<Booking> findByOwnerIdItemAndStatus(Long ownerId, String status);
}
