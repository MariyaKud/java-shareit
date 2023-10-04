package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ContextShareIt;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@DisplayName("Item controller")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class   ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final Validator validator;

    private Set<ConstraintViolation<ItemDto>> validates;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    private static final long userId = 1L;

    private final LocalDateTime current = LocalDateTime.now();

    private final ItemDto itemDto = new ItemDto(1L, "name", "description",
                                                      true, null);

    private final ItemDtoWithBookings itemDtoWithBookings = ItemDtoWithBookings.builder()
                                                                .id(itemDto.getId())
                                                                .name(itemDto.getName())
                                                                .description(itemDto.getDescription())
                                                                .available(itemDto.getAvailable())
                                                                .lastBooking(null)
                                                                .nextBooking(null)
                                                                .comments(Collections.emptySet())
                                                                .build();

    @Test
    @DisplayName("should create item")
    void should_Create_Item() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemService, times(1)).createItem(anyLong(), any());
    }

    @Test
    @DisplayName("should update item")
    void should_Update_Item() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));

        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("should get item by id for owner item")
    void should_Get_Item_ById_For_Owner_Id() throws Exception {
        when(itemService.getItemByIdForUserId(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBookings);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoWithBookings)));

        verify(itemService, times(1)).getItemByIdForUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("should get list item by owner id")
    void should_Get_Items_By_Owner_Id() throws Exception {
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoWithBookings));

        mockMvc.perform(get("/items")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoWithBookings))));

        verify(itemService, times(1))
                .getItemsByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should search list items contain text for exist user ")
    void should_Search_Items_By_Exist_UserId() throws Exception {
        when(itemService.searchItemsForUserWithId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("text", "name")
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService, times(1))
                .searchItemsForUserWithId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should create comment")
    void should_Create_Comment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "text", "author", current);

        when(itemService.createComment(anyLong(), anyLong(), any(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(commentDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemService, times(1)).createComment(anyLong(), anyLong(), any(), any());
    }

    @DisplayName("should not validate id for item")
    @Test
    public void should_not_validate_id() {
        final ItemDto valueToCheck = new ItemDto();
        valueToCheck.setId(-1L);
        valueToCheck.setName("name");
        valueToCheck.setDescription("description");
        valueToCheck.setAvailable(true);
        valueToCheck.setRequestId(1L);

        validates = validator.validate(valueToCheck, Update.class);

        assertEquals("id", validates.iterator().next().getPropertyPath().toString(),
                "должно быть больше 0");
    }

    @Test
    @DisplayName("should not get all requests")
    void should_not_search_items() throws Exception {
        when(itemService.searchItemsForUserWithId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("text", "name")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Mistake for empty name")
    @Test
    void should_not_validate_empty_name() {
        final ItemDto valueToCheck = new ItemDto();
        valueToCheck.setId(itemDto.getId());
        valueToCheck.setName("");
        valueToCheck.setDescription("description");
        valueToCheck.setAvailable(true);
        valueToCheck.setRequestId(1L);

        validates = validator.validate(valueToCheck, Update.class);

        assertTrue(validates.size() > 0);
        assertEquals("name", validates.iterator().next().getPropertyPath().toString(),
                "размер должен находиться в диапазоне от 1 до 100");
    }

    @DisplayName("Mistake for empty description")
    @Test
    void should_not_validate_empty_description() {
        final ItemDto valueToCheck = new ItemDto();
        valueToCheck.setId(itemDto.getId());
        valueToCheck.setName("name");
        valueToCheck.setDescription("");
        valueToCheck.setAvailable(true);
        valueToCheck.setRequestId(1L);

        validates = validator.validate(valueToCheck, Update.class);

        assertTrue(validates.size() > 0);
        assertEquals("description", validates.iterator().next().getPropertyPath().toString(),
                "размер должен находиться в диапазоне от 1 до 1000");
    }

    @DisplayName("Mistake for available null")
    @Test
    void should_not_validate_empty_available_null() {
        final ItemDto valueToCheck = new ItemDto();
        valueToCheck.setId(itemDto.getId());
        valueToCheck.setName("name");
        valueToCheck.setDescription("Description");
        valueToCheck.setAvailable(null);
        valueToCheck.setRequestId(1L);

        validates = validator.validate(valueToCheck, Create.class);

        assertTrue(validates.size() > 0);
        assertEquals("available", validates.iterator().next().getPropertyPath().toString(),
                "не должно равняться null");
    }
}