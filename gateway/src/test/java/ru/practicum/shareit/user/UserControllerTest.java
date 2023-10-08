package ru.practicum.shareit.user;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.client.ContextShareIt;
import ru.practicum.shareit.user.dto.UserDto;

@DisplayName("User controller")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    UserClient userClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public static final long userId = 1L;

    private final UserDto userDto = new UserDto(1L,"John","john.doe@mail.com");

    ResponseEntity<Object> response = new ResponseEntity<>(userDto, HttpStatus.OK);

    @Test
    @DisplayName("should validate request to create user")
    void should_create_user() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userClient, times(1)).createUser(any());
    }

    @Test
    @DisplayName("should validate request to update user")
    void should_update_user() throws Exception {
        when(userClient.updateUser(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/users/{userId}",userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userClient, times(1)).updateUser(anyLong(), any());
    }

    @Test
    @DisplayName("should validate request to delete user")
    void should_delete_user() throws Exception {
        when(userClient.delUser(anyLong()))
                .thenReturn(response);

        mockMvc.perform(delete("/users/{Id}",userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).delUser(anyLong());
    }

    @Test
    @DisplayName("should validate request to get users")
    void should_return_all_users() throws Exception {
        when(userClient.getUsers(anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/users")
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                )
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUsers(ContextShareIt.from, ContextShareIt.size);
    }

    @Test
    @DisplayName("should not validate request to get users because from not correct")
    void should_not_return_all_users_from() throws Exception {
        when(userClient.getUsers(anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/users")
                        .param("from", String.valueOf(-1))
                        .param("size", String.valueOf(ContextShareIt.size))
                )
                .andExpect(status().isInternalServerError());

        verify(userClient, times(0)).getUsers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("should not validate request to get users because from not correct")
    void should_not_return_all_users_size() throws Exception {
        when(userClient.getUsers(anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/users")
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isInternalServerError());

        verify(userClient, times(0)).getUsers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("should validate request to get user by id")
    void should_get_user_by_id() throws Exception {
        when(userClient.getUser(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userClient, times(1)).getUser(anyLong());
    }

    @DisplayName("should not validate id request to create user")
    @Test
    public void should_not_validate_id() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(response);

        final UserDto badDto = new UserDto(-1L, userDto.getName(), userDto.getEmail());

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any());
    }

    @DisplayName("should not validate for empty User request to create user")
    @Test
    void should_not_validate_empty_user_to_create() throws Exception {
        final UserDto badDto = new UserDto();

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any());
    }

    @DisplayName("Mistake for empty Email request to create user")
    @Test
    void should_not_validate_empty_email() throws Exception {
        final UserDto badDto = new UserDto(userDto.getId(), userDto.getName(), "");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any());
    }

    @DisplayName("Mistake for Email without @ request to create user")
    @Test
    void should_not_validate_email_without_dog() throws Exception {
        final UserDto badDto = new UserDto(userDto.getId(), userDto.getName(), "myemail");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any());
    }

    @DisplayName("Mistake for Email with @ request to create user")
    @Test
    public void should_not_validate_email_with_dog() throws Exception {
        final UserDto badDto = new UserDto(userDto.getId(), userDto.getName(), "это-неправильный?Email@");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(badDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(any());
    }
}