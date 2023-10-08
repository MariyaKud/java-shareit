package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@DisplayName("Item controller")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class   ItemControllerTest {

    @MockBean
    ItemClient itemClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final long userId = 1L;

    private final ItemDto itemDto = new ItemDto(1L, "name", "description",
                                                      true, null);

    ResponseEntity<Object> response = new ResponseEntity<>(itemDto, HttpStatus.OK);

    @Test
    @DisplayName("should validate request to create item")
    void should_create_item() throws Exception {
        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(response);

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

        verify(itemClient, times(1)).createItem(anyLong(), any());
    }

    @Test
    @DisplayName("should validate request to update item")
    void should_update_item() throws Exception {
        when(itemClient.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(response);

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

        verify(itemClient, times(1)).updateItem(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("should validate request get item by id for owner item")
    void should_get_item_by_id_for_owner_id() throws Exception {
        when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(anyLong(), anyLong());
    }

    @Test
    @DisplayName("should validate request get list item by owner id")
    void should_get_items_by_owner_id() throws Exception {
        when(itemClient.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/items")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should search list items contain text for exist user ")
    void should_Search_Items_By_Exist_UserId() throws Exception {
        when(itemClient.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("text", "name")
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient, times(1))
                .searchItems(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should validate request to create comment")
    void should_Create_Comment() throws Exception {
        final CommentDto commentDto = new CommentDto("text");

        final ResponseEntity<Object> responseCommentDto = new ResponseEntity<>(commentDto, HttpStatus.OK);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(responseCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createComment(anyLong(), anyLong(), any());
    }

    @DisplayName("should not validate id for item")
    @Test
    public void should_not_validate_id() throws Exception {
        final ItemDto dto = new ItemDto();
        dto.setId(-1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).createComment(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("should not get all requests")
    void should_not_search_items() throws Exception {
        when(itemClient.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("text", "name")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Mistake for empty name")
    @Test
    void should_not_validate_empty_name() throws Exception {
        final ItemDto dto = new ItemDto();
        dto.setId(itemDto.getId());
        dto.setName("");
        dto.setDescription("description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).createComment(anyLong(), anyLong(), any());
    }

    @DisplayName("Mistake for empty description")
    @Test
    void should_not_validate_empty_description() throws Exception {
        final ItemDto dto = new ItemDto();
        dto.setId(itemDto.getId());
        dto.setName("name");
        dto.setDescription("");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).createComment(anyLong(), anyLong(), any());
    }

    @DisplayName("Mistake for available null")
    @Test
    void should_not_validate_empty_available_null() throws Exception {
        final ItemDto dto = new ItemDto();
        dto.setId(itemDto.getId());
        dto.setName("name");
        dto.setDescription("Description");
        dto.setAvailable(null);
        dto.setRequestId(1L);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).createComment(anyLong(), anyLong(), any());
    }
}