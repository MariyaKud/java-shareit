package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("Item request repository")
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    User user1;
    ItemRequest request1;
    ItemRequest request2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@email"));

        request1 = itemRequestRepository.save(ItemRequest.builder()
                                        .author(user1)
                                        .created(LocalDateTime.now())
                                        .description("description 1")
                                        .build());

        request2 = itemRequestRepository.save(ItemRequest.builder()
                                        .author(user1)
                                        .created(LocalDateTime.now().plusMinutes(5))
                                        .description("description 2")
                                        .build());
    }

    @Test
    @DisplayName("should find requests by author id")
    void should_find_by_author_id() {
        List<ItemRequest> requests = itemRequestRepository.findByAuthorIdOrderByCreatedDesc(user1.getId());

        assertEquals(2, requests.size());
        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
        assertEquals(user1.getId(), requests.get(0).getAuthor().getId());
        assertEquals(user1.getId(), requests.get(1).getAuthor().getId());
    }

    @Test
    @DisplayName("should find requests by author not equal id")
    void should_find_author_not_equal_id() {
        final Page<ItemRequest> requests = itemRequestRepository.findByAuthorIdNot(user1.getId(), Pageable.unpaged());

        assertEquals(0, requests.getTotalElements());
    }
}