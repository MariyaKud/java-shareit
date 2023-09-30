package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("Booking repository")
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;
    User user3;
    Item item1;
    Booking booking1;
    Booking booking2;

    private final LocalDateTime current = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@email"));
        user2 = userRepository.save(new User(2L, "user 2", "user2@email"));
        user3 = userRepository.save(new User(3L, "user 3", "user3@email"));

        item1 = itemRepository.save(Item.builder()
                .id(1L)
                .owner(user1)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .build());

        booking1 = bookingRepository.save(Booking.builder()
                                    .id(1L)
                                    .item(item1)
                                    .booker(user2)
                                    .start(current.plusDays(1))
                                    .end(current.plusDays(2))
                                    .status(StatusBooking.APPROVED)
                                    .build());

        booking2 = bookingRepository.save(Booking.builder()
                .id(2L)
                .item(item1)
                .booker(user3)
                .start(current.plusDays(3))
                .end(current.plusDays(4))
                .status(StatusBooking.WAITING)
                .build());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBeforeAndStatus() {
        boolean existsBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                                        user2.getId(), item1.getId(), current.plusDays(3), StatusBooking.APPROVED);
        assertTrue(existsBooking);
    }

    @Test
    void existsByItemIdInPeriodFromStartToEnd() {
        boolean existsBooking = bookingRepository.existsByItemIdInPeriodFromStartToEnd(
                                                  item1.getId(), current, current.plusDays(3));
        assertTrue(existsBooking);
    }

    @Test
    void findByItemsBooking() {
        List<Booking> bookings = bookingRepository.findByItemsBooking(Set.of(item1.getId()),
                                                                      StatusBooking.APPROVED.name());

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBookerId() {
        Page<Booking> bookings = bookingRepository.findByBookerId(user3.getId(), Pageable.unpaged());

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findByBookerIdAndEndBefore() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndEndBefore(user2.getId(), current.plusDays(3),
                                                                               Pageable.unpaged());
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findByBookerIdAndStartAfter() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartAfter(user2.getId(), current,
                                                                                Pageable.unpaged());
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findByBookerIdAndStatus() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStatus(user2.getId(), StatusBooking.APPROVED,
                                                                            Pageable.unpaged());
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual() {
        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(
           user2.getId(), current.plusDays(1).plusMinutes(60), current.plusDays(1).plusMinutes(60), Pageable.unpaged());

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findBookingByIdForOwner() {
        Optional<Booking> bookings = bookingRepository.findBookingByIdForOwner(booking1.getId(), user1.getId());

        assertTrue(bookings.isPresent());
        assertNotNull(bookings.get());
    }

    @Test
    void findByOwnerId() {
        Page<Booking> bookings = bookingRepository.findByOwnerId(user1.getId(), Pageable.unpaged());

        assertEquals(2, bookings.getTotalElements());
    }

    @Test
    void findByOwnerIdCurrent() {
        Page<Booking> bookings = bookingRepository.findByOwnerIdCurrent(user1.getId(),
                                  current.plusDays(1).plusMinutes(10), Pageable.unpaged());

        assertEquals(1, bookings.getTotalElements());
        assertEquals(booking1, bookings.getContent().get(0));
    }

    @Test
    void findByOwnerIdFuture() {
        Page<Booking> bookings = bookingRepository.findByOwnerIdFuture(user1.getId(),
                                  current.plusDays(2), Pageable.unpaged());

        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void findByOwnerIdPast() {
        Page<Booking> bookings = bookingRepository.findByOwnerIdPast(user1.getId(),
                                  current.plusDays(5), Pageable.unpaged());

        assertEquals(2, bookings.getTotalElements());
    }

    @Test
    void findByOwnerIdAndStatus() {
        Page<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(user1.getId(),
                                  StatusBooking.WAITING.toString(), Pageable.unpaged());

        assertEquals(1, bookings.getTotalElements());
    }
}