package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisplayName("Test User service full")
@TestMethodOrder(OrderAnnotation.class)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserServiceImpl service;

    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setEmail("test@email");
    }

    @Test
    @Order(value = 1)
    @DisplayName("should create user from dto")
    @Rollback(false)
    void should_create_user() {
        System.out.println("should create user from dto");

        userDto = service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Order(value = 2)
    @DisplayName("should return all users")
    void should_return_all_users() {
        System.out.println("should return all users");
        PageRequest pageRequest = PageRequest.ofSize(10);

        List<UserDto> userDtos = service.getAllUsers(pageRequest);

        userDtos.forEach(u -> System.out.println(u.getId() + ": " + u.getName()));

        assertThat(userDtos.size(), equalTo(1));
        assertThat(userDtos.get(0), equalTo(userDto));
    }

    @Test
    @Order(value = 3)
    @DisplayName("should update email and name user from dto")
    void should_Update_Email_User() {
        System.out.println("should update email and name user from dto");

        userDto.setEmail("new@mail.ru");
        userDto.setName("new");

        userDto = service.updateUser(userDto.getId(), userDto);

        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User userUpdate = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(userUpdate.getId(), equalTo(userDto.getId()));
        assertThat(userUpdate.getName(), equalTo(userDto.getName()));
        assertThat(userUpdate.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Order(value = 4)
    @DisplayName("should return user by id")
    void should_Return_User() {
        System.out.println("should return user");

        UserDto user = service.getUserById(userDto.getId());

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Order(value = 5)
    @DisplayName("should delete user by id")
    void deleteUserById() {
        System.out.println("should delete user by id");

        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));

        service.deleteUserById(userDto.getId());


        Throwable exception = assertThrows(NoResultException.class, () ->
                               query.setParameter("id", userDto.getId()).getSingleResult());

        assertThat(exception.getMessage(), equalTo("No entity found for query"));
    }
}