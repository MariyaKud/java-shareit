package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test Booking service full")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingServiceImpl service;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    final LocalDateTime current = LocalDateTime.now();

    User owner;

    User booker;

    Item item;

    Booking booking;

    BookingDtoOut bookingDtoOut;

    BookingDto bookingDtoIn;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(new User(1L, "user 1", "user1@email"));

        booker = userRepository.save(new User(2L, "user 2", "user2@email"));

        item = itemRepository.save(Item.builder()
                .id(1L)
                .owner(owner)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .comments(Set.of())
                .build());

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(current)
                .end(current.plusDays(1))
                .status(StatusBooking.APPROVED)
                .build();

        bookingDtoIn = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(item.getId())
                .build();
    }

    @Test
    @Order(value = 1)
    @DisplayName("should create booking")
    @Rollback(false)
    void should_create_booking() {
        bookingDtoOut = service.createBooking(booker.getId(), bookingDtoIn, current);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking createBooking = query.setParameter("id", bookingDtoOut.getId()).getSingleResult();

        assertThat(1L, equalTo(createBooking.getId()));
        assertThat(booker, equalTo(createBooking.getBooker()));
        assertThat(item, equalTo(createBooking.getItem()));
        assertThat(bookingDtoIn.getStart(), equalTo(createBooking.getStart()));
        assertThat(bookingDtoIn.getEnd(), equalTo(createBooking.getEnd()));
        assertThat(StatusBooking.WAITING, equalTo(createBooking.getStatus()));
    }

    @Test
    @Order(value = 2)
    @DisplayName("should unapproved booking")
    @Rollback(false)
    void should_approved_booking() {
        bookingDtoOut = service.approvedBooking(owner.getId(), bookingDtoIn.getId(), false);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking approvedBooking = query.setParameter("id", bookingDtoOut.getId()).getSingleResult();

        assertThat(StatusBooking.REJECTED, equalTo(approvedBooking.getStatus()));
    }

    @Test
    @Order(value = 3)
    @DisplayName("get all rejected bookings ")
    void get_bookings_by_booker_id() {

        List<BookingDtoOut> bookingDtoOuts = service.getBookingsByBookerId(booker.getId(),
                                                      StateBooking.REJECTED, 0, 10);

        assertThat(bookingDtoOuts.size(), equalTo(1));
        assertThat(bookingDtoOuts.get(0).getStatus().name(), equalTo("REJECTED"));
        assertThat(bookingDtoOuts.get(0).getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    @Order(value = 4)
    @DisplayName("should find booking by id for owner")
    void should_get_bookings_by_owner_id() {

       BookingDtoOut bookingById = service.getBookingById(owner.getId(), bookingDtoIn.getId());

        assertThat(bookingById.getId(), equalTo(bookingDtoIn.getId()));

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemBooking = query.setParameter("id", bookingById.getItem().getId()).getSingleResult();

        assertThat(itemBooking.getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    @Order(value = 5)
    @DisplayName("should get bookings by id for booker")
    void should_throw_exception_then_bookings_by_booker_id() {

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getBookingsByBookerId(booker.getId(),
                        StateBooking.UN_KNOW, 0, 10));

        Assertions.assertEquals(StateBooking.class.getName() + " с id = 0 не найден.", exception.getMessage());

    }

    @Test
    @Order(value = 6)
    @DisplayName("should get bookings by id for owner")
    void should_throw_exception_then_bookings_by_owner_id() {

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getBookingsByOwnerId(booker.getId(),
                        StateBooking.UN_KNOW, 0, 10));

        Assertions.assertEquals(StateBooking.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }
}