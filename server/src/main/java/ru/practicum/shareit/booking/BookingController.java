package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.ContextShareIt;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                       @RequestBody BookingDtoIn bookingDto) {
        return bookingService.createBooking(userId, bookingDto, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approvedBooking(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getBookingsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL") StateBooking state,
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getBookingsForItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                              @RequestParam(defaultValue = "ALL") StateBooking state,
                                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingsByOwnerId(userId, state, from, size);
    }
}
