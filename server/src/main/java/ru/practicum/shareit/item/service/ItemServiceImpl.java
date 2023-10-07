package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoOutWithBookings;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.exeption.ItemUnavailable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository requestRepository;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User owner = findUserById(userId);

        itemDto.setId(null);

        final Item item = itemMapper.fromDto(owner, itemDto);

        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> itemRequest = requestRepository.findById(itemDto.getRequestId());
            itemRequest.ifPresent(item::setRequest);
        }

        final Item newItem = itemRepository.save(item);

        return itemMapper.toDto(newItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        existsUserById(userId);

        final String name = itemDto.getName();
        final String description = itemDto.getDescription();
        final Item updateItem = findItemById(itemId);

        if (userId != updateItem.getOwner().getId()) {
            throw new ItemBelongsAnotherOwner();
        }
        if (name != null && !name.isBlank()) {
            updateItem.setName(name);
        }
        if (description != null && !description.isBlank()) {
            updateItem.setDescription(description);
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(updateItem);

        return itemMapper.toDto(updateItem);
    }

    @Override
    public ItemDtoOutWithBookings getItemByIdForUserId(long userId, long itemId) {
        List<Booking> bookings;
        LocalDateTime current = LocalDateTime.now();

        existsUserById(userId);

        Item item = findItemById(itemId);
        if (userId == item.getOwner().getId()) {
            bookings = bookingRepository.findByItemsBooking(Stream.of(itemId).collect(Collectors.toSet()),
                                                             String.valueOf(StatusBooking.APPROVED));
        } else {
            bookings = Collections.emptyList();
        }

        return itemMapper.toDtoWithBooking(item,bookings,current);
    }

    @Override
    public List<ItemDtoOutWithBookings> getItemsByUserId(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        LocalDateTime current = LocalDateTime.now();

        Map<Long, Item>  itemIds = itemRepository.findByOwner_Id(userId, pageable)
                                                 .stream()
                                                 .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Booking>> itemBookings = bookingRepository.findByItemsBooking(itemIds.keySet(),
                                                                   String.valueOf(StatusBooking.APPROVED))
                                                 .stream()
                                                 .collect(Collectors.groupingBy(f -> f.getItem().getId()));

        return itemIds.entrySet()
                      .stream()
                      .map(f -> itemMapper.toDtoWithBooking(f.getValue(),
                                  itemBookings.getOrDefault(f.getKey(), Collections.emptyList()), current))
                      .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsForUserWithId(long userId, String text, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        existsUserById(userId);

        if (text.isBlank()) {
            return List.of();
        }

        return (itemRepository.findByAvailableTrue_And_ContainingText(text, page)
                              .stream()
                              .map(itemMapper::toDto)
                              .collect(Collectors.toList()));
    }

    @Override
    public CommentDtoOut createComment(long userId, long itemId, CommentDtoIn commentDto, LocalDateTime current) {
        User author = findUserById(userId);
        Item item = findItemById(itemId);


        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(userId, itemId,
                                current, StatusBooking.APPROVED)) {

            throw new ItemUnavailable(itemId);

        } else {

            Comment comment = commentRepository.save(itemMapper.commentFromDto(commentDto, item, author, current));

            if (item.getComments() == null) {
                Set<Comment> comments = new HashSet<>();
                item.setComments(comments);
            }

            item.getComments().add(comment);

            itemRepository.save(item);

            return  itemMapper.commentToDto(comment);
        }
    }

    private User findUserById(long userId) {
        return  userRepository.findById(userId)
                              .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                             .orElseThrow(() -> new EntityNotFoundException(itemId, Item.class));
    }

    private void existsUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId, User.class);
        }
    }
}
