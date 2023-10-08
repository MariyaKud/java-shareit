package ru.practicum.shareit.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;

@DisplayName("ItemRequest controller")
@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    ItemRequestClient requestClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final long firstId = 1L;

    private final ItemRequestDto requestDto = new ItemRequestDto("description");

    ResponseEntity<Object> response = new ResponseEntity<>(requestDto, HttpStatus.OK);

    @Test
    @DisplayName("should validate request to create item request")
    void should_create_item_request() throws Exception {
        when(requestClient.createItemRequest(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                                .header(ContextShareIt.HEADER_USER_ID, firstId)
                                .content(mapper.writeValueAsString(requestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk());

        verify(requestClient, times(1)).createItemRequest(anyLong(), any());
    }

    @Test
    @DisplayName("should not validate request to create item request")
    void should_not_create_item_request() throws Exception {
        final ItemRequestDto dto = new ItemRequestDto();

        when(requestClient.createItemRequest(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(requestClient, times(0)).createItemRequest(anyLong(), any());
    }

    @Test
    @DisplayName("should not create with empty description")
    void should_not_create_with_empty_description() throws Exception {
        final ItemRequestDto dto = new ItemRequestDto("");

        when(requestClient.createItemRequest(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(requestClient, times(0)).createItemRequest(anyLong(), any());
    }

    @Test
    @DisplayName("should get item requests for author id")
    void should_get_item_requests_bu_author_id() throws Exception {
        when(requestClient.getItemRequests(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/requests", firstId)
                                .header(ContextShareIt.HEADER_USER_ID, firstId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getItemRequests(anyLong());
    }

    @Test
    @DisplayName("should get all requests")
    void should_get_all_item_requests() throws Exception {
        when(requestClient.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                      )
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getAllItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should get item request by id")
    void should_get_item_request_by_id() throws Exception {
        when(requestClient.getItemRequest(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", firstId)
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getItemRequest(anyLong(), anyLong());
    }

    @Test
    @DisplayName("should not get all requests")
    void should_nt_get_all_item_requests() throws Exception {
        when(requestClient.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isInternalServerError());

        verify(requestClient, times(0)).getAllItemRequests(anyLong(), anyInt(), anyInt());
    }
}