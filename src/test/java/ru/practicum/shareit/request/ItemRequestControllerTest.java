package ru.practicum.shareit.request;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.ContextShareIt;

@DisplayName("ItemRequest controller")
@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final long userId = 1L;

    private final LocalDateTime current = LocalDateTime.now();

    private final ItemRequestDtoOut requestDto = new ItemRequestDtoOut(1L, "description", current);

    private final ItemRequestDtoWithItems requestDtoWithItems = ItemRequestDtoWithItems.builder()
                                                                  .id(requestDto.getId())
                                                                  .description(requestDto.getDescription())
                                                                  .created(requestDto.getCreated())
                                                                  .items(Collections.emptyList())
                                                                  .build();

    @Test
    @DisplayName("should create item request")
    void should_Create_ItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                                .header(ContextShareIt.HEADER_USER_ID, userId)
                                .content(mapper.writeValueAsString(requestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(requestDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));

        verify(itemRequestService, times(1)).createItemRequest(anyLong(), any(), any());
    }

    @Test
    @DisplayName("should get item requests for user id")
    void should_Get_ItemRequests_For_UserId() throws Exception {
        when(itemRequestService.getMyItemRequests(anyLong()))
                .thenReturn(List.of(requestDtoWithItems));

        mockMvc.perform(get("/requests", requestDtoWithItems.getId())
                                .header(ContextShareIt.HEADER_USER_ID, userId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDtoWithItems))));

        verify(itemRequestService, times(1)).getMyItemRequests(anyLong());
    }

    @Test
    @DisplayName("should get all requests")
    void should_Get_All_ItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDtoWithItems));

        mockMvc.perform(get("/requests/all")
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .param("from", String.valueOf(ContextShareIt.from))
                        .param("size", String.valueOf(ContextShareIt.size))
                      )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDtoWithItems))));

        verify(itemRequestService, times(1)).getAllItemRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should get item request by id")
    void should_Get_ItemRequest_ById_ForUserId() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDtoWithItems);

        mockMvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(requestDtoWithItems.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(requestDtoWithItems)));

        verify(itemRequestService, times(1)).getRequestById(anyLong(), anyLong());
    }
}