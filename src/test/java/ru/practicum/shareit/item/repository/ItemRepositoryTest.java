package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("Item repository")
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User user1;
    Item item1;
    User user2;
    Item item2;

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

        user2 = userRepository.save(new User(2L, "user 2", "user2@email"));
        item2 = itemRepository.save(Item.builder()
                .id(2L)
                .owner(user2)
                .name("item 2")
                .description("item 2 description")
                .available(true)
                .build());
    }

    @DisplayName("should find item by owner")
    @Test
    void should_find_item_by_owner() {
        final Page<Item> byOwner = itemRepository.findByOwner_Id(user1.getId(), Pageable.unpaged());

        assertEquals(1, byOwner.getTotalElements());
        assertEquals(item1,byOwner.getContent().get(0));
    }
}