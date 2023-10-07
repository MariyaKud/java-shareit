package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOutShort;
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
                .requestId((item.getRequest() == null) ? null : item.getRequest().getId())
                .build();
    }

    public ItemDtoOut toShortDto(Item item) {
        return new ItemDtoOut(item.getId(), item.getName());
    }

    public ItemDtoOutWithBookings toDtoWithBooking(Item item, List<Booking> bookings, LocalDateTime current) {
        Optional<Booking> last =  bookings.stream()
                                          .filter(f -> !f.getStart().isAfter(current))
                                          .reduce((first, second) -> second);

        Optional<Booking> next = bookings.stream()
                                         .filter(f -> f.getStart().isAfter(current))
                                         .findFirst();

        Set<CommentDtoOut> comments = item.getComments().stream().map(this::commentToDto).collect(Collectors.toSet());

        return ItemDtoOutWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(toDtoBooking(last.orElse(null)))
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

    private static BookingDtoOutShort toDtoBooking(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return BookingDtoOutShort.builder()
                    .id(booking.getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .bookerId(booking.getBooker().getId())
                    .build();
        }
    }

    public Comment commentFromDto(CommentDtoIn commentDto, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .created(created)
                .build();
    }

    public CommentDtoOut commentToDto(Comment comment) {
        return new CommentDtoOut(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
