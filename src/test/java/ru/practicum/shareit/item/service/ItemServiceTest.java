package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Item service")
class ItemServiceTest {

    ItemServiceImpl itemService;

    ItemRepository itemRepository;

    UserRepository userRepository;

    BookingRepository bookingRepository;

    CommentRepository commentRepository;

    ItemRequestRepository requestRepository;

    ItemMapper itemMapper;

    final LocalDateTime current = LocalDateTime.now();

    User user;

    User guest;

    ItemDto itemDtoWithoutRequest;

    ItemDto itemDtoByRequest;

    Item item;

    ItemRequest request1;

    ItemDtoWithBookings itemDtoWithBooking;

    Comment comment;
    CommentDtoShort commentDto;

    CommentDto commentDtoOut;

    Booking booking;

    BookingDtoShort bookingDtoOut;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        itemMapper = mock(ItemMapper.class);

        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                                          commentRepository, requestRepository, itemMapper);

        user = new User(1L, "user 1", "user1@email");

        guest = new User(2L, "guest", "guest@email");

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .build();

        request1 = ItemRequest.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .description("description 1")
                .build();

        itemDtoWithoutRequest = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        itemDtoByRequest = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(request1.getId())
                .build();

        comment = Comment.builder()
                .id(1L)
                .item(item)
                .text("text")
                .created(current)
                .author(guest)
                .build();

        commentDto = new CommentDtoShort(comment.getText());

        commentDtoOut = new CommentDto(comment.getId(), comment.getText(),
                                       comment.getAuthor().getName(), comment.getCreated());

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(guest)
                .start(current)
                .end(current.plusDays(1))
                .status(StatusBooking.WAITING)
                .build();

        bookingDtoOut = BookingDtoShort.builder()
                .id(1L)
                .bookerId(guest.getId())
                .start(current)
                .end(current.plusDays(1))
                .build();

        itemDtoWithBooking = ItemDtoWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookingDtoOut)
                .build();
    }

    @Test
    void should_throw_exception_if_create_item_and_owner_not_found() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), User.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.createItem(1L, itemDtoWithoutRequest));

        Assertions.assertEquals(User.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    void should_create_item_without_request() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(itemMapper.toDto(any())).thenReturn(itemDtoWithoutRequest);

        ItemDto createItem = itemService.createItem(user.getId(), itemDtoWithoutRequest);

        Assertions.assertEquals(createItem, itemDtoWithoutRequest);

        verify(itemRepository, times(1))
                .save(any());

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(itemMapper, times(1))
                .fromDto(user, itemDtoWithoutRequest);

        verify(itemMapper, times(1))
                .toDto(any());

        verify(requestRepository, times(0))
                .findById(anyLong());
    }

    @Test
    void should_create_item_by_exist_request() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(itemMapper.fromDto(any(), any())).thenReturn(item);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request1));

        when(itemMapper.toDto(any())).thenReturn(itemDtoByRequest);

        ItemDto createItem = itemService.createItem(user.getId(), itemDtoByRequest);

        Assertions.assertEquals(createItem, itemDtoByRequest);
        Assertions.assertEquals(createItem.getRequestId(), request1.getId());

        verify(itemRepository, times(1))
                .save(any());

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(itemMapper, times(1))
                .fromDto(user, itemDtoByRequest);

        verify(itemMapper, times(1))
                .toDto(any());

        verify(requestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void should_create_item_by_not_exist_request() {
        Optional<ItemRequest> optEmpty = Optional.empty();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(itemMapper.fromDto(any(), any())).thenReturn(item);

        when(requestRepository.findById(anyLong())).thenReturn(optEmpty);

        when(itemMapper.toDto(any())).thenReturn(itemDtoByRequest);

        ItemDto createItem = itemService.createItem(user.getId(), itemDtoByRequest);

        Assertions.assertEquals(createItem, itemDtoByRequest);

        verify(itemRepository, times(1))
                .save(any());

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(itemMapper, times(1))
                .fromDto(user, itemDtoByRequest);

        verify(itemMapper, times(1))
                .toDto(any());

        verify(requestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void should_throw_exception_update_item_if_user_not_found() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), itemDtoByRequest));

        Assertions.assertEquals(User.class.getName() + " с id = 1 не найден.", exception.getMessage());
    }

    @Test
    void should_throw_exception_update_item_if_item_not_found() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenThrow(new EntityNotFoundException(anyLong(), Item.class));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), itemDtoByRequest));

        Assertions.assertEquals(Item.class.getName() + " с id = 0 не найден.", exception.getMessage());
    }

    @Test
    void should_throw_exception_update_item_not_owner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final ItemBelongsAnotherOwner exception = Assertions.assertThrows(
                ItemBelongsAnotherOwner.class,
                () -> itemService.updateItem(100L, item.getId(), itemDtoByRequest));

        Assertions.assertEquals("Не корректно указан владелец вещи.", exception.getMessage());
    }

    @Test
    void should_update_item() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemRepository.save(any())).thenReturn(item);

        when(itemMapper.toDto(any())).thenReturn(itemDtoByRequest);

        ItemDto updateItem = itemService.updateItem(user.getId(), item.getId(), itemDtoByRequest);

        Assertions.assertEquals(updateItem, itemDtoByRequest);

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(itemRepository, times(1))
                .findById(anyLong());

        verify(itemRepository, times(1))
                .save(any());

        verify(itemMapper, times(1))
                .toDto(any());
    }

    @Test
    void should_create_comment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(guest));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                              .thenReturn(true);

        when(commentRepository.save(any())).thenReturn(comment);

        when(itemRepository.save(any())).thenReturn(item);

        when(itemMapper.commentToDto(comment)).thenReturn(commentDtoOut);

        CommentDto newComment = itemService.createComment(guest.getId(), item.getId(), commentDto, current);

        Assertions.assertEquals(newComment, commentDtoOut);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(itemRepository, times(1))
                .findById(anyLong());

        verify(bookingRepository, times(1))
                .existsByBookerIdAndItemIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any());

        verify(commentRepository, times(1))
                .save(any());

        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void get_items_by_user_id() {
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));

        when(itemRepository.findByOwner_Id(anyLong(), any())).thenReturn(itemPage);

        when(bookingRepository.findByItemsBooking(anySet(), anyString())).thenReturn(List.of(booking));

        when(itemMapper.toDtoWithBooking(any(), anyList(), any())).thenReturn(itemDtoWithBooking);

        List<ItemDtoWithBookings> items = itemService.getItemsByUserId(user.getId(), 0, 10);

        Assertions.assertEquals(items.size(), 1);

        Assertions.assertEquals(items.get(0).getLastBooking(), bookingDtoOut);

        verify(itemRepository, times(1))
                .findByOwner_Id(anyLong(), any());

        verify(bookingRepository, times(1))
                .findByItemsBooking(anySet(), anyString());

        verify(itemMapper, times(1))
                .toDtoWithBooking(any(), anyList(), any());
    }

    @Test
    void get_item_by_id_for_user_id() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findByItemsBooking(anySet(), anyString())).thenReturn(List.of(booking));

        when(itemMapper.toDtoWithBooking(any(), anyList(), any())).thenReturn(itemDtoWithBooking);

        itemService.getItemByIdForUserId(guest.getId(), item.getId());

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(bookingRepository, times(0))
                .findByItemsBooking(anySet(), anyString());

        verify(itemMapper, times(1))
                .toDtoWithBooking(any(), anyList(), any());
    }

    @Test
    void get_item_by_id_for_owner_id() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findByItemsBooking(anySet(), anyString())).thenReturn(List.of(booking));

        when(itemMapper.toDtoWithBooking(any(), anyList(), any())).thenReturn(itemDtoWithBooking);

        itemService.getItemByIdForUserId(user.getId(), item.getId());

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(bookingRepository, times(1))
                .findByItemsBooking(anySet(), anyString());

        verify(itemMapper, times(1))
                .toDtoWithBooking(any(), anyList(), any());
    }

    @Test
    void search_items_for_user_with_id() {
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.findByAvailableTrue_And_ContainingText(anyString(), any())).thenReturn(itemPage);

        itemService.searchItemsForUserWithId(user.getId(), "text", 0, 10);

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(itemRepository, times(1))
                .findByAvailableTrue_And_ContainingText(anyString(), any());
    }
}