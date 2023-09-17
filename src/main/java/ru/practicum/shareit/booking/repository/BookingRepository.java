package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId,
                                                           LocalDateTime current, StatusBooking status);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime current);

    @Query(value = "SELECT * FROM bookings " +
                   "WHERE user_id = :bookerId AND " +
                   "start_data <= :current AND end_data >= :current " +
                   "ORDER BY start_data DESC",
            nativeQuery = true)
    List<Booking> findByBookerIdAndStateCurrent(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    @Query(value = "SELECT * FROM bookings as b " +
                   "JOIN items as i on b.item_id = i.id " +
                   "WHERE b.id = :bookerId AND (b.user_id = :ownerId or i.user_id = :ownerId)",
            nativeQuery = true)
    Optional<Booking> findBookingByIdForOwner(Long bookerId, Long ownerId);

    @Query(value = "SELECT case when count(b.id) > 0 then true else false end FROM bookings as b " +
                   "WHERE b.item_id = :itemId AND " +
                   "((b.start_data BETWEEN :start and :end) " +
                   " OR (b.end_data BETWEEN :start and :end) " +
                   " OR (b.start_data >= :start and b.end_data <= :end)) ",
            nativeQuery = true)
    boolean existsByItemIdInPeriodFromStartToEnd(Long itemId, LocalDateTime start, LocalDateTime end);

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
            "WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
            "AND end_data < :current " +
            "ORDER BY start_data DESC ",
            nativeQuery = true)
    List<Booking> findByOwnerIdItemPast(Long ownerId, LocalDateTime current);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
            "AND start_data > :current " +
            "ORDER BY start_data DESC ",
            nativeQuery = true)
    List<Booking> findByOwnerIdItemFuture(Long ownerId, LocalDateTime current);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) AND " +
            "start_data <= :current AND end_data >= :current " +
            "ORDER BY start_data DESC",
            nativeQuery = true)
    List<Booking> findByOwnerIdItemCurrent(Long ownerId, LocalDateTime current);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id in (SELECT i.id FROM items AS i WHERE i.user_id = :ownerId) AND status = :status " +
            "ORDER BY start_data DESC ",
            nativeQuery = true)
    List<Booking> findByOwnerIdItemAndStatus(Long ownerId, String status);
}