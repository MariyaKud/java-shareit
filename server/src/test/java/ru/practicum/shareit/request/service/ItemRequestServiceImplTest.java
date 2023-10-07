package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
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
@DisplayName("Test Request service full")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestServiceImpl service;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    final LocalDateTime current = LocalDateTime.of(2023, 10, 27, 10, 40, 55);

    ItemRequest itemRequest;

    ItemRequestDtoOut requestOut;

    ItemRequestDtoIn requestIn;

    User author;


    @BeforeEach
    void beforeEach() {
        author = userRepository.save(new User(1L, "user 1", "user1@email"));

        itemRequest = ItemRequest.builder()
                .id(1L)
                .author(author)
                .created(current)
                .description("description")
                .build();

        requestIn = new ItemRequestDtoIn("description");
    }

    @Test
    @Order(value = 1)
    @DisplayName("should create request")
    @Rollback(false)
    void should_create_item_request() {

        requestOut = service.createItemRequest(author.getId(), requestIn, current);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest createRequest = query.setParameter("id", 1L).getSingleResult();

        assertThat(1L, equalTo(createRequest.getId()));
        assertThat(requestIn.getDescription(), equalTo(createRequest.getDescription()));
    }

    @Test
    @Order(value = 2)
    @DisplayName("should throw exception not found user")
    void should_throw_exception_get_request_by_id_for_not_exist_user() {
        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getRequestById(10L, 1L));

        Assertions.assertEquals(User.class.getName() + " с id = 10 не найден.", exception.getMessage());
    }

    @Test
    @Order(value = 3)
    @DisplayName("should throw exception not found booking")
    void should_throw_exception_get_request_by_id_for_not_exist_booking() {
        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.getRequestById(1L, 10L));

        Assertions.assertEquals(ItemRequest.class.getName() + " с id = 10 не найден.", exception.getMessage());
    }

    @Test
    @Order(value = 4)
    @DisplayName("should throw exception not found booking")
    void should_get_request_by_id() {
        final ItemRequestDtoOutWithItems requestDtoWithItems = service.getRequestById(1L, 1L);

        assertThat(1L, equalTo(requestDtoWithItems.getId()));

        final ItemRequestDtoOutWithItems requestToCheck = mapper.toDtoWithItems(itemRequest, List.of());
        assertThat(requestToCheck.getId(), equalTo(requestDtoWithItems.getId()));
        assertThat(requestToCheck.getDescription(), equalTo(requestDtoWithItems.getDescription()));
        assertThat(requestToCheck.getCreated(), equalTo(requestDtoWithItems.getCreated()));
        assertThat(requestDtoWithItems.getItems().size(), equalTo(0));
    }
}