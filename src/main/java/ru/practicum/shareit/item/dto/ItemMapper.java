package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemDtoShort toShortDto(Item item) {
        return new ItemDtoShort(item.getId(), item.getName());
    }

    public ItemWithBookings toDtoWithBooking(Item item, List<Booking> bookings, LocalDateTime current) {
        List<Booking> lasts = bookings.stream()
                .filter(f -> !f.getStart().isAfter(current))
                .collect(Collectors.toList());

        Booking last;
        if (lasts.size() == 0) {
            last = null;
        } else {
            last = lasts.get(lasts.size() - 1);
        }

        Optional<Booking> next = bookings.stream()
                                         .filter(f -> f.getStart().isAfter(current))
                                         .findFirst();

        Set<CommentDto> comments = item.getComments().stream().map(this::commentToDto).collect(Collectors.toSet());

        return ItemWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(toDtoBooking(last))
                .nextBooking(toDtoBooking(next.orElse(null)))
                .comments(comments)
                .build();
    }

    public Item fromDto(User user, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    private static BookingDtoShort toDtoBooking(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return BookingDtoShort.builder()
                    .id(booking.getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .bookerId(booking.getBooker().getId())
                    .build();
        }
    }

    public Comment commentFromDto(CommentDtoShort commentDto, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .created(created)
                .build();
    }

    public CommentDto commentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
