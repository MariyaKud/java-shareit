package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
    public BookingDtoOut createBooking(Long bookerId, BookingDto bookingDto, LocalDateTime current) {
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
    public BookingDtoOut approvedBooking(Long userId, Long bookingId, Boolean approved) {
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
    public BookingDtoOut getBookingById(Long ownerId, Long bookingId) {
        return bookingMapper.toDto(findBookingByIdForOwner(bookingId, ownerId));
    }

    @Override
    public List<BookingDtoOut> getBookingsByBookerId(Long bookerId, StateBooking stateBooking, int from, int size) {
        final LocalDateTime current = LocalDateTime.now();
        Page<Booking> result;
        findUserById(bookerId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size,
                                           Sort.by("start").descending());

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(bookerId, current,
                                                                                                     current, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartAfter(bookerId, current, pageable);
                break;
            case PAST:
                result = bookingRepository.findByBookerIdAndEndBefore(bookerId, current, pageable);
                break;
            case WAITING:
                result = bookingRepository.findByBookerIdAndStatus(bookerId, StatusBooking.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findByBookerIdAndStatus(bookerId, StatusBooking.REJECTED, pageable);
                break;
            default:
                throw new EntityNotFoundException(0L, StateBooking.class);
        }

        return bookingMapper.bookingsToDto(result);
    }

    @Override
    public List<BookingDtoOut> getBookingsByOwnerId(Long ownerId, StateBooking stateBooking, int from, int size) {
        final LocalDateTime current = LocalDateTime.now();
        Page<Booking> result;
        findUserById(ownerId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size,
                            Sort.by("start_data").descending());

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findByOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findByOwnerIdCurrent(ownerId, current, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findByOwnerIdFuture(ownerId, current, pageable);
                break;
            case PAST:
                result = bookingRepository.findByOwnerIdPast(ownerId, current, pageable);
                break;
            case WAITING:
                result = bookingRepository.findByOwnerIdAndStatus(ownerId,
                         String.valueOf(StatusBooking.WAITING), pageable);
                break;
            case REJECTED:
                result = bookingRepository.findByOwnerIdAndStatus(ownerId,
                         String.valueOf(StatusBooking.REJECTED), pageable);
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
