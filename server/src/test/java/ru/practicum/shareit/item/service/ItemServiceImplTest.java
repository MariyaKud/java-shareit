package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exeption.ItemUnavailable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test Item service full")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemServiceImpl service;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    final LocalDateTime current = LocalDateTime.of(2023, 10, 27, 10, 40, 55);

    User user1;

    User user2;

    Item item;

    ItemDto itemDto;

    CommentDtoIn commentDtoShort;

    CommentDtoOut commentDto;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@email"));

        user2 = userRepository.save(new User(2L, "user 2", "user2@email"));

        item = Item.builder()
                .id(1L)
                .owner(user1)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .build();

        commentDtoShort = new CommentDtoIn("description");
    }

    @Test
    @Order(value = 1)
    @Rollback(false)
    void should_create_item() {
        itemDto = itemMapper.toDto(item);
        itemDto = service.createItem(user1.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemInBase = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(itemInBase.getId()));
    }

    @Test
    @Order(value = 2)
    void search_items_for_user_with_empty_text() {
        List<ItemDto> items = service.searchItemsForUserWithId(user1.getId(), "", 0, 10);

        assertThat(items.size(), equalTo(0));
    }

    @Test
    @Order(value = 3)
    void should_throw_item_unavailable_when_create_comment() {
        final ItemUnavailable exception = Assertions.assertThrows(
                ItemUnavailable.class,
                () -> service.createComment(1L, 1L, commentDtoShort, current));

        Assertions.assertEquals("Вещь с id: 1 не доступна для бронирования", exception.getMessage());
    }

    @Test
    @Order(value = 4)
    @Rollback(false)
    void should_create_comment() {

        final Booking booking = bookingRepository.save(Booking.builder()
                .id(1L)
                .item(item)
                .booker(user2)
                .start(current.minusDays(2))
                .end(current.minusDays(1))
                .status(StatusBooking.APPROVED)
                .build());

        commentDto = service.createComment(user2.getId(), 1L, commentDtoShort, current);

        TypedQuery<Comment> query = em.createQuery("Select i from Comment i where i.id = :id", Comment.class);
        Comment commentInBase = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(commentInBase.getId()));
    }

    @Test
    @Order(value = 5)
    @Rollback(false)
    void should_get_item_by_id() {
        ItemDtoOutWithBookings itemByIdForUserId = service.getItemByIdForUserId(user1.getId(), item.getId());

        assertThat(item.getId(), equalTo(itemByIdForUserId.getId()));
        assertThat(1L, equalTo(itemByIdForUserId.getNextBooking().getId()));
    }
}