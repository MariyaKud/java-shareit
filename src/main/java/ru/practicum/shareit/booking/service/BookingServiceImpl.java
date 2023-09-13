package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exeption.AttemptApprovedNotFromOwnerItem;
import ru.practicum.shareit.booking.exeption.NoAccessBooking;
import ru.practicum.shareit.booking.exeption.NotCorrectApproved;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.EntityNotFoundException;

import ru.practicum.shareit.item.exeption.ItemUnavailable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingDtoView createBooking(Long bookerId, BookingDto bookingDto, LocalDateTime current) {
        final User booker = findUserById(bookerId);
        final Item item = findItemById(bookingDto.getItemId());

        final LocalDateTime start = bookingDto.getStart();
        final LocalDateTime end = bookingDto.getEnd();

        if (!item.getAvailable()) {
            throw new ItemUnavailable(item.getId());
        }

        if (!checkItemFree(item.getId(), start, end)) {
            throw new NoAccessBooking(bookerId, item.getId());
        }

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NoAccessBooking(bookerId, item.getId());
        }

        bookingDto.setId(null);

        final Booking newBooking = bookingRepository.save(bookingMapper.fromDto(bookingDto, booker,
                                                           item, StatusBooking.WAITING));

        return bookingMapper.toDto(newBooking);
    }

    @Override
    public BookingDtoView approvedBooking(Long userId, Long bookingId, Boolean approved) {
        findUserById(userId);

        final Booking booking = findBookingById(bookingId);
        final Item item = booking.getItem();

        if (booking.getStatus() == StatusBooking.APPROVED) {
            throw new NotCorrectApproved(bookingId);
        }
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new AttemptApprovedNotFromOwnerItem(userId, item.getId());
        }
        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDtoView getBookingById(Long ownerId, Long bookingId) {
        return bookingMapper.toDto(findBookingByIdForOwner(bookingId, ownerId));
    }

    @Override
    public List<BookingDtoView> getBookingsByUserId(Long bookerId, StateBooking stateBooking) {
        final LocalDateTime current = LocalDateTime.now();
        List<Booking> result;
        findUserById(bookerId);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                result = bookingRepository.findByBookerIdAndStateCurrent(bookerId, current);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, current);
                break;
            case PAST:
                result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, current);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.REJECTED);
                break;
            default:
                throw new EntityNotFoundException(0L, StateBooking.class);
        }

        return bookingMapper.bookingsToDto(result);
    }

    @Override
    public List<BookingDtoView> getBookingsForItemsByUserId(Long ownerId, StateBooking stateBooking) {
        final LocalDateTime current = LocalDateTime.now();
        List<Booking> result;
        findUserById(ownerId);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findByOwnerIdItem(ownerId);
                break;
            case CURRENT:
                result = bookingRepository.findByOwnerIdItemCurrent(ownerId, current);
                break;
            case FUTURE:
                result = bookingRepository.findByOwnerIdItemFuture(ownerId, current);
                break;
            case PAST:
                result = bookingRepository.findByOwnerIdItemPast(ownerId, current);
                break;
            case WAITING:
                result = bookingRepository.findByOwnerIdItemAndStatus(ownerId, String.valueOf(StatusBooking.WAITING));
                break;
            case REJECTED:
                result = bookingRepository.findByOwnerIdItemAndStatus(ownerId, String.valueOf(StatusBooking.REJECTED));
                break;
            default:
                throw new EntityNotFoundException(0L, StateBooking.class);
        }

        return bookingMapper.bookingsToDto(result);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(itemId, Item.class));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, Booking.class));
    }

    private Booking findBookingByIdForOwner(long bookingId, Long ownerId) {
        return bookingRepository.findBookingByIdForOwner(bookingId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, Booking.class));
    }

    private boolean checkItemFree(Long itemId, LocalDateTime start, LocalDateTime end) {
        return !bookingRepository.existsByItemIdInPeriodFromStartToEnd(itemId, start, end);
    }
}
