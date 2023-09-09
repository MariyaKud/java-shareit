package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.ContextShareIt;
import ru.practicum.shareit.validation.Create;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoView createBooking(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                        @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        //новый запрос в статусе WAITING
        return bookingService.createBooking(userId, bookingDto, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoView approvedBooking(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        //подтверждение только владельцем
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoView getBooking(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        //либо автор бронирования, либо владелец
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoView> getBookingsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL") StateBooking state) {
        //отсортированы по дате от более новых к более старым
        return bookingService.getBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoView> getBookingsForItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                       @RequestParam(defaultValue = "ALL") StateBooking state) {
        //бронь вещей пользователя
        return bookingService.getBookingsForItemsByUserId(userId, state);
    }
}
