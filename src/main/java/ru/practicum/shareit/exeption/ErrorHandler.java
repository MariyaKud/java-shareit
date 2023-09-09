package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.exeption.AttemptApprovedNotFromOwnerItem;
import ru.practicum.shareit.booking.exeption.NoAccessBooking;
import ru.practicum.shareit.booking.exeption.NotCorrectApproved;
import ru.practicum.shareit.booking.exeption.NotCorrectBooking;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.exeption.ItemUnavailable;
import ru.practicum.shareit.validation.ContextShareIt;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class,
                       ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        log.error("Ошибка при валидации {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка при валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Ошибка при валидации {}", e.getMessage(), e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : allErrors) {
            for (Object argument : Objects.requireNonNull(error.getArguments())) {
                errorMessage.append(argument).append(" ");
            }
            errorMessage.append(error.getDefaultMessage()).append(";");
        }
        return new ErrorResponse(errorMessage.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(String.format("Unknown %s: %s",  e.getName(), e.getValue()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("Не указан заголовок {}", e.getMessage(), e);
        return new ErrorResponse("Не указан заголовок " + ContextShareIt.HEADER_USER_ID);
    }

    @ExceptionHandler({NotCorrectBooking.class,
                       ItemUnavailable.class,
                       NotCorrectApproved.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAttemptApprovedNotFromOwnerItem(final RuntimeException e) {
        log.error("Нет прав на выполняемое действие {}", e.getMessage(), e);
        return new ErrorResponse("Нет прав: " + e.getMessage());
    }

    @ExceptionHandler ({EntityNotFoundException.class,
                        NoSuchElementException.class,
                        NoAccessBooking.class,
                        AttemptApprovedNotFromOwnerItem.class,
                        ItemBelongsAnotherOwner.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("Получен статус 500 {}", e.getMessage(), e);
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}
