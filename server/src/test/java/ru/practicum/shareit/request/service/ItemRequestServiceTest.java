package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Item request service")
class ItemRequestServiceTest {

    ItemRequestServiceImpl requestService;

    ItemRequestRepository requestRepository;

    UserRepository userRepository;

    ItemRepository itemRepository;

    ItemRequestMapper requestMapper;

    ItemMapper itemMapper;

    final LocalDateTime current = LocalDateTime.now();

    User user;

    User owner;

    ItemRequest itemRequest;

    ItemRequestDtoIn itemRequestDtoIn;

    ItemRequestDtoOut itemRequestDtoOut;

    ItemRequestDtoOutWithItems itemRequestDto;

    Item item;

    ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        requestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        requestMapper = mock(ItemRequestMapper.class);
        itemMapper = mock(ItemMapper.class);

        requestService = new ItemRequestServiceImpl(requestRepository, userRepository,
                                                    itemRepository, requestMapper, itemMapper);

        user = new User(1L, "user 1", "user1@email");
        owner = new User(2L, "user 2", "user2@email");

        itemRequest = ItemRequest.builder()
                .id(1L)
                .author(user)
                .created(current)
                .description("description")
                .build();

        itemRequestDtoIn = new ItemRequestDtoIn(itemRequest.getDescription());

        itemRequestDtoOut = new ItemRequestDtoOut();
        itemRequestDtoOut.setId(itemRequest.getId());
        itemRequestDtoOut.setDescription(itemRequest.getDescription());
        itemRequestDtoOut.setCreated(itemRequest.getCreated());

        itemRequestDto = new ItemRequestDtoOutWithItems();
        itemRequestDto.setId(itemRequestDtoOut.getId());
        itemRequestDto.setDescription(itemRequestDtoOut.getDescription());
        itemRequestDto.setCreated(itemRequestDtoOut.getCreated());

        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item 1")
                .description("item 1 description")
                .available(true)
                .request(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    @Test
    void should_create_item_request() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(requestMapper.fromDto(any(), any(), any())).thenReturn(itemRequest);

        when(requestRepository.save(any())).thenReturn(itemRequest);

        when(requestMapper.toDto(any())).thenReturn(itemRequestDtoOut);

        ItemRequestDtoOut createRequest = requestService.createItemRequest(user.getId(), itemRequestDtoIn, current);

        Assertions.assertEquals(createRequest, itemRequestDtoOut);

        verify(userRepository, times(1))
                .findById(anyLong());

        verify(requestMapper, times(1))
                .fromDto(any(), any(), any());

        verify(requestRepository, times(1))
                .save(any());

        verify(requestMapper, times(1))
                .toDto(any());
    }

    @Test
    void get_requests_by_user_id_without_items() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestRepository.findByAuthorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        when(itemRepository.findByRequest_Ids(anySet())).thenReturn(List.of());

        when(requestMapper.toDtoWithItems(any(), any())).thenReturn(itemRequestDto);

        List<ItemRequestDtoOutWithItems> requests = requestService.getMyItemRequests(user.getId());

        Assertions.assertEquals(requests.size(), 1);
        Assertions.assertEquals(requests.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requests.get(0).getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(requests.get(0).getDescription(), itemRequestDto.getDescription());
        Assertions.assertNull(requests.get(0).getItems());

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(requestRepository, times(1))
                .findByAuthorIdOrderByCreatedDesc(anyLong());

        verify(itemRepository, times(1))
                .findByRequest_Ids(anySet());

        verify(requestMapper, times(1))
                .toDtoWithItems(any(), any());
    }

    @Test
    void get_all_requests_another_user() {
        PageImpl<ItemRequest> itemRequests = new PageImpl<>(Collections.singletonList(itemRequest));

        when(requestRepository.findByAuthorIdNot(anyLong(), any())).thenReturn(itemRequests);

        when(itemRepository.findByRequest_Ids(anySet())).thenReturn(List.of(item));

        when(itemMapper.toDto(any())).thenReturn(itemDto);

        itemRequestDto.setItems(List.of(itemDto));
        when(requestMapper.toDtoWithItems(any(), any())).thenReturn(itemRequestDto);

        List<ItemRequestDtoOutWithItems> requests = requestService.getAllItemRequests(user.getId(), 0, 10);

        Assertions.assertEquals(requests.size(), 1);
        Assertions.assertEquals(requests.get(0).getId(), itemRequestDto.getId());
        Assertions.assertEquals(requests.get(0).getCreated(), itemRequestDto.getCreated());
        Assertions.assertEquals(requests.get(0).getDescription(), itemRequestDto.getDescription());
        Assertions.assertEquals(requests.get(0).getItems().size(), 1);

        verify(requestRepository, times(1))
                .findByAuthorIdNot(anyLong(), any());

        verify(itemRepository, times(1))
                .findByRequest_Ids(anySet());

        verify(itemMapper, times(1))
                .toDto(any());

        verify(requestMapper, times(1))
                .toDtoWithItems(any(), any());
    }

    @Test
    void get_request_by_id() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        when(requestMapper.toDtoWithItems(any(), any())).thenReturn(itemRequestDto);

        ItemRequestDtoOutWithItems result = requestService.getRequestById(user.getId(), itemRequest.getId());

        Assertions.assertEquals(result, itemRequestDto);

        verify(userRepository, times(1))
                .existsById(anyLong());

        verify(requestRepository, times(1))
                .findById(anyLong());

        verify(requestMapper, times(1))
                .toDtoWithItems(any(), any());
    }
}