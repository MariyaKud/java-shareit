package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("Item repository")
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    Item item1;
    User user2;
    Item item2;
    Comment comment1;
    ItemRequest request1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@email"));

        item1 = itemRepository.save(Item.builder()
                .id(1L)
                .owner(user1)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .build());

        request1 = itemRequestRepository.save(ItemRequest.builder()
                .id(1L)
                .author(user1)
                .created(LocalDateTime.now())
                .description("description")
                .build());

        user2 = userRepository.save(new User(2L, "user 2", "user2@email"));

        item2 = itemRepository.save(Item.builder()
                .id(2L)
                .owner(user2)
                .request(request1)
                .name("item 2")
                .description("item 2 description")
                .available(true)
                .build());

        comment1 = commentRepository.save(Comment.builder()
                .text("comment 1")
                .author(user2)
                .item(item1)
                .created(LocalDateTime.now())
                .build());

        item1.setComments(Set.of(comment1));
    }

    @DisplayName("should find item by owner")
    @Test
    void should_find_items_With_Text() {
        final Page<Item> byOwner = itemRepository.findByAvailableTrue_And_ContainingText("ite", Pageable.unpaged());

        assertEquals(2, byOwner.getTotalElements());
        assertEquals(item1,byOwner.getContent().get(0));
        assertEquals(item2,byOwner.getContent().get(1));

    }

    @DisplayName("should find item by owner")
    @Test
    void should_Find_Item_By_Owner() {
        final Page<Item> byOwner = itemRepository.findByOwner_Id(user1.getId(), Pageable.unpaged());

        assertEquals(1, byOwner.getTotalElements());
        assertEquals(item1,byOwner.getContent().get(0));
    }

    @DisplayName("should find items by request ids")
    @Test
    void should_Find_Items_By_RequestIds() {
        final List<Item> byRequestId = itemRepository.findByRequest_Ids(Set.of(request1.getId()));

        assertEquals(1, byRequestId.size());
        assertEquals(item2,byRequestId.get(0));
    }

    @DisplayName("should find items by request id")
    @Test
    void should_Find_Items_By_RequestId() {
        final List<Item> byRequestId = itemRepository.findByRequest_Id(request1.getId());

        assertEquals(1, byRequestId.size());
        assertEquals(item2,byRequestId.get(0));
    }

    @DisplayName("should get item by id")
    @Test
    void should_Get_Items_By_Id() {
        final Optional<Item> item = itemRepository.findById(item1.getId());

        assertEquals(item1, item.get());
        assertEquals(1,item.get().getComments().size());
    }
}