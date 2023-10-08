package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exeption.AttemptApprovedNotFromOwnerItem;
import ru.practicum.shareit.booking.exeption.NotCorrectApproved;
import ru.practicum.shareit.booking.exeption.NoAccessBooking;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.exeption.ItemUnavailable;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ItemUnavailable.class,
                       NotCorrectApproved.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        log.error("No access {}", e.getMessage(), e);
        return new ErrorResponse("No access: " + e.getMessage());
    }

    @ExceptionHandler ({EntityNotFoundException.class,
                        NoSuchElementException.class,
                        NoAccessBooking.class,
                        ItemBelongsAnotherOwner.class,
                        AttemptApprovedNotFromOwnerItem.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.debug("Got status 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("Got status 500 {}", e.getMessage(), e);
        return new ErrorResponse(
                "An unexpected error has occurred."
        );
    }
}
