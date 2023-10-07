package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.common.ContextShareIt;
import ru.practicum.shareit.common.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@DisplayName("Test User controller")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final Validator validator;

    private Set<ConstraintViolation<UserDto>> validates;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    private final UserDto userDto = new UserDto(1L,"John","john.doe@mail.com");

    @Test
    @DisplayName("should create user")
    void should_create_user() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    @DisplayName("should update user")
    void should_update_user() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}",userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @Test
    @DisplayName("should delete user")
    void should_delete_user() throws Exception {
        when(userService.deleteUserById(anyLong()))
                .thenReturn(true);

        mockMvc.perform(delete("/users/{Id}",userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    @DisplayName("should return empty users")
    void should_return_all_users() throws Exception {
        final PageRequest pageable = PageRequest.of(ContextShareIt.from > 0 ?
                                      ContextShareIt.from / ContextShareIt.size : 0, ContextShareIt.size);

        List<UserDto> expectedResult = Collections.emptyList();
        when(userService.getAllUsers(pageable))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/users")
                            .param("from", String.valueOf(ContextShareIt.from))
                            .param("size", String.valueOf(ContextShareIt.size))
                        )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        verify(userService, times(1)).getAllUsers(pageable);
    }

    @Test
    @DisplayName("should find user by id")
    void should_get_user_by_id() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @DisplayName("should not validate id")
    @Test
    public void should_not_validate_id() {
        final UserDto valueToCheck = new UserDto(-1L, userDto.getName(), userDto.getEmail());
        validates = validator.validate(valueToCheck, Create.class);

        assertEquals("id", validates.iterator().next().getPropertyPath().toString(),
                 "должно быть больше 0");
    }

    @DisplayName("should not validate for empty User")
    @Test
    void should_not_validate_empty_user_to_create() {
        final UserDto valueToCheck = new UserDto();

        validates = validator.validate(valueToCheck, Create.class);
        assertTrue(validates.size() > 0);
    }

    @DisplayName("Mistake for empty Email")
    @Test
    void should_not_validate_empty_email() {
        final UserDto valueToCheck = new UserDto(userDto.getId(), userDto.getName(), "");

        validates = validator.validate(valueToCheck, Create.class);

        assertTrue(validates.size() > 0);
        assertEquals("email", validates.iterator().next().getPropertyPath().toString(),
                "размер должен находиться в диапазоне от 1 до 512");
    }

    @DisplayName("Mistake for Email without @")
    @Test
    void should_not_validate_email_without_dog() {
        final UserDto valueToCheck = new UserDto(userDto.getId(), userDto.getName(), "myemail");

        validates = validator.validate(valueToCheck, Create.class);

        assertTrue(validates.size() > 0);
        assertEquals("email",
                validates.iterator().next().getPropertyPath().toString(),
                "должно иметь формат адреса электронной почты");
    }

    @DisplayName("Mistake for Email with @")
    @Test
    public void should_not_validate_email_with_dog() {
        final UserDto valueToCheck = new UserDto(userDto.getId(), userDto.getName(), "это-неправильный?Email@");

        validates = validator.validate(valueToCheck, Create.class);

        assertTrue(validates.size() > 0);
        assertEquals("email",
                validates.iterator().next().getPropertyPath().toString(),
                "должно иметь формат адреса электронной почты");
    }

    public static final long userId = 1L;
}