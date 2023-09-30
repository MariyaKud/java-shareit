package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exeption.AttemptApprovedNotFromOwnerItem;
import ru.practicum.shareit.booking.exeption.NoAccessBooking;
import ru.practicum.shareit.booking.exeption.NotCorrectApproved;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.exeption.ItemUnavailable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Test booking Service")
class BookingServiceTest {
    BookingServiceImpl bookingService;

    BookingRepository bookingRepository;

    UserRepository userRepository;

    ItemRepository itemRepository;

    BookingMapper bookingMapper;

    User owner;

    User booker;

    Item item;

    ItemDtoShort itemOut;

    UserDtoShort bookerOut;

    Booking booking;

    BookingDtoOut bookingOut;

    BookingDto bookingIn;

    private final LocalDateTime current = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);

        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);

        owner = new User(1L, "owner", "owner@email");

        booker = new User(2L, "booker", "booker1@email");

        bookerOut = new UserDtoShort(booker.getId(),booker.getEmail());

        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .build();

        itemOut = new ItemDtoShort(item.getId(), item.getName());

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(current)
                .end(current.plusDays(1))
                .status(StatusBooking.WAITING)
                .build();

        bookingOut = BookingDtoOut.builder()
                .id(1L)
                .item(itemOut)
                .booker(bookerOut)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(StatusBooking.APPROVED)
                .build();

        bookingIn = BookingDto.builder()
                .id(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    @Test
    void should_throw_not_found_user_if_create_booking_and_booker_not_found() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), User.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), bookingIn, current));

        Assertions.assertEquals(User.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    void should_throw_not_found_item_if_create_booking_and_item_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(itemRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), Item.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), bookingIn, current));

        Assertions.assertEquals(Item.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    void should_throw_item_unavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final ItemUnavailable exception = Assertions.assertThrows(
                ItemUnavailable.class,
                () -> bookingService.createBooking(1L, bookingIn, current));

        Assertions.assertEquals("Вещь с id: 1 не доступна для бронирования", exception.getMessage());
    }

    @Test
    void should_throw_no_access_booking_if_item_not_free() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdInPeriodFromStartToEnd(any(), any(), any())).thenReturn(true);

        Assertions.assertThrows(
                NoAccessBooking.class,
                () -> bookingService.createBooking(1L, bookingIn, current));
    }

    @Test
    void should_throw_no_access_booking_for_owner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdInPeriodFromStartToEnd(any(), any(), any())).thenReturn(false);

        Assertions.assertThrows(
                NoAccessBooking.class,
                () -> bookingService.createBooking(owner.getId(), bookingIn, current));
    }

    @Test
    void should_create_bookings_for_available_and_free_item() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdInPeriodFromStartToEnd(any(), any(), any())).thenReturn(false);

        when(bookingRepository.save(any())).thenReturn(booking);

        when(bookingMapper.fromDto(any(), any(), any(), any())).thenReturn(booking);

        when(bookingMapper.toDto(any())).thenReturn(bookingOut);

        BookingDtoOut bookingResult = bookingService.createBooking(booker.getId(), bookingIn, current);

        Assertions.assertEquals(bookingOut, bookingResult);

        verify(itemRepository, times(1))
                .findById(anyLong());

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(1))
                .existsByItemIdInPeriodFromStartToEnd(any(), any(), any());

        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void should_approved_booking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        when(bookingRepository.save(any())).thenReturn(booking);

        when(bookingMapper.toDto(any())).thenReturn(bookingOut);

        BookingDtoOut bookingResult = bookingService.approvedBooking(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(bookingOut, bookingResult);
        Assertions.assertEquals(bookingOut.getStatus(), StatusBooking.APPROVED);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(1))
                .findById(any());

        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void should_throw_booking_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), Booking.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.approvedBooking(owner.getId(), booking.getId(), true));

        Assertions.assertEquals(Booking.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    void should_throw_no_correct_approved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        booking.setStatus(StatusBooking.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final NotCorrectApproved exception = Assertions.assertThrows(
                NotCorrectApproved.class,
                () -> bookingService.approvedBooking(owner.getId(), booking.getId(), true));

        Assertions.assertEquals("Для брони с id: 1 уже установлен статус подтвержден, изменить статус нельзя",
                                    exception.getMessage());
    }

    @Test
    void should_throw_exception_if_try_approved_not_owner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        final AttemptApprovedNotFromOwnerItem exception = Assertions.assertThrows(
                AttemptApprovedNotFromOwnerItem.class,
                () -> bookingService.approvedBooking(booker.getId(), booking.getId(), true));

        Assertions.assertEquals("Попытка подтвердить бронирование от пользователя с id: 2, " +
                                         "хотя он не владелец бронируемой вещи c id: 1",
                                          exception.getMessage());
    }

    @Test
    void should_get_booking_by_id() {
        when(bookingRepository.findBookingByIdForOwner(anyLong(),anyLong())).thenReturn(Optional.of(booking));

        when(bookingMapper.toDto(any())).thenReturn(bookingOut);

        bookingService.getBookingById(bookingOut.getId(), owner.getId());

        verify(bookingRepository, times(1))
                .findBookingByIdForOwner(anyLong(), anyLong());

        verify(bookingMapper, times(1))
                .toDto(any());
    }

    @Test
    void should_find_all_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.ALL, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(1))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_current_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.CURRENT, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_future_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.FUTURE, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_past_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.PAST, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_rejected_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.REJECTED, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_waiting_bookings_by_booker_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByBookerId(booker.getId(), StateBooking.WAITING, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByBookerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(anyLong(), any(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_all_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.ALL, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(1))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_current_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.CURRENT, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(1))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_past_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.PAST, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_future_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.FUTURE, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void should_find_rejected_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.REJECTED, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByOwnerIdAndStatus(anyLong(), anyString(), any());
    }

    @Test
    void should_find_waiting_bookings_by_owner_id() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.getBookingsByOwnerId(booker.getId(), StateBooking.WAITING, 0, 10);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(0))
                .findByOwnerId(anyLong(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdCurrent(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdFuture(anyLong(), any(), any());

        verify(bookingRepository, times(0))
                .findByOwnerIdPast(anyLong(), any(), any());

        verify(bookingRepository, times(1))
                .findByOwnerIdAndStatus(anyLong(), anyString(), any());
    }
}