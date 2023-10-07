package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime current, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime current, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, StatusBooking status, Pageable pageable);

    Page<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStart(
                  Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT * FROM bookings as b " +
                   "JOIN items as i on b.item_id = i.id " +
                   "WHERE b.id = :bookerId AND (b.user_id = :ownerId or i.user_id = :ownerId)",
            nativeQuery = true)
    Optional<Booking> findBookingByIdForOwner(Long bookerId, Long ownerId);

    @Query(value = "SELECT * FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId)",
          countQuery = "SELECT count(*) FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId)",
          nativeQuery = true)
    Page<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) AND " +
                   "start_data <= :current AND end_data >= :current ",
    countQuery = "SELECT count(*) FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) AND " +
                 "start_data <= :current AND end_data >= :current ",
           nativeQuery = true)
    Page<Booking> findByOwnerIdCurrent(Long ownerId, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
                   "AND start_data > :current ",
      countQuery = "SELECT count(*) FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
                   "AND start_data > :current ",
           nativeQuery = true)
    Page<Booking> findByOwnerIdFuture(Long ownerId, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
                   "AND end_data < :current ",
      countQuery = "SELECT count(*) FROM bookings WHERE item_id in (SELECT id FROM items WHERE user_id = :ownerId) " +
                   "AND end_data < :current ",
           nativeQuery = true)
    Page<Booking> findByOwnerIdPast(Long ownerId, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id in (SELECT i.id FROM items AS i WHERE i.user_id = :ownerId) " +
                   "AND status = :status ",
      countQuery = "SELECT count(*) FROM bookings WHERE item_id in (SELECT i.id FROM items AS i " +
                   "WHERE i.user_id = :ownerId) AND status = :status ",
           nativeQuery = true)
    Page<Booking> findByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);
}