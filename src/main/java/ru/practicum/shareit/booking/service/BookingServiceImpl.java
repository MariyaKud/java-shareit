package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exeption.AttemptApprovedNotFromOwnerItem;
import ru.practicum.shareit.booking.exeption.NoAccessBooking;
import ru.practicum.shareit.booking.exeption.NotCorrectApproved;
import ru.practicum.shareit.booking.exeption.NotCorrectBooking;
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
import java.util.stream.Collectors;

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

        if (start.isBefore(current) || end.isBefore(start) || end.equals(start)) {
            throw new NotCorrectBooking(bookingDto.getStart(), bookingDto.getEnd());
        }

        if (!checkItemFree(item.getId(), start, end)) {
            throw new NoAccessBooking(bookerId, item.getId());
        }

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NoAccessBooking(bookerId, item.getId());
        }

        bookingDto.setId(null);
        bookingDto.setStatus(StatusBooking.WAITING);

        final Booking newBooking = bookingRepository.save(bookingMapper.fromDto(bookingDto, booker, item));

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
        final Booking booking = findBookingById(bookingId);

        if (!Objects.equals(ownerId, booking.getBooker().getId()) &&
                !Objects.equals(ownerId, booking.getItem().getOwner().getId())) {
            throw new NoAccessBooking(ownerId, bookingId);
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDtoView> getBookingsByUserId(Long bookerId, StateBooking stateBooking) {
        findUserById(bookerId);

        if (stateBooking == StateBooking.ALL) {

            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId)
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.CURRENT ||
                    stateBooking == StateBooking.FUTURE ||
                     stateBooking == StateBooking.PAST) {

            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId)
                    .stream()
                    .map(bookingMapper::toDto)
                    .filter(b -> b.isRightState(stateBooking))
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.WAITING) {

            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.WAITING)
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.REJECTED) {

            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, StatusBooking.REJECTED)
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else {
            throw new EntityNotFoundException(0L, StateBooking.class);
        }
    }

    @Override
    public List<BookingDtoView> getBookingsForItemsByUserId(Long ownerId, StateBooking stateBooking) {
        findUserById(ownerId);

        if (stateBooking == StateBooking.ALL) {

            return bookingRepository.findByOwnerIdItem(ownerId)
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.CURRENT ||
                stateBooking == StateBooking.FUTURE ||
                stateBooking == StateBooking.PAST) {

            return bookingRepository.findByOwnerIdItem(ownerId)
                    .stream()
                    .map(bookingMapper::toDto)
                    .filter(b -> b.isRightState(stateBooking))
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.WAITING) {

            return bookingRepository.findByOwnerIdItemAndStatus(ownerId, String.valueOf(StatusBooking.WAITING))
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else if (stateBooking == StateBooking.REJECTED) {

            return bookingRepository.findByOwnerIdItemAndStatus(ownerId, String.valueOf(StatusBooking.REJECTED))
                    .stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());

        } else {
            throw new EntityNotFoundException(0L, StateBooking.class);
        }
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

    private boolean checkItemFree(Long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStartAndEndBetween(itemId, start, end);
        return bookings.size() == 0;
    }
}
