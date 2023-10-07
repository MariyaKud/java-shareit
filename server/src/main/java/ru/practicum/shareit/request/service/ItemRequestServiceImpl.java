package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithItems;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper requestMapper;

    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDtoOut createItemRequest(long userId, ItemRequestDtoIn itemRequestDto, LocalDateTime current) {
        User author = findUserById(userId);

        ItemRequest itemRequest = requestMapper.fromDto(author, itemRequestDto, current);

        return requestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoOutWithItems> getMyItemRequests(long userId) {
        existsUserById(userId);

        Map<Long, ItemRequest> itemRequests = requestRepository.findByAuthorIdOrderByCreatedDesc(userId)
                                                               .stream()
                                                               .collect(Collectors.toMap(ItemRequest::getId,
                                                                         Function.identity()));
        return enrichRequestsByItems(itemRequests);
    }

    @Override
    public List<ItemRequestDtoOutWithItems> getAllItemRequests(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());

        Map<Long, ItemRequest> itemRequests = requestRepository.findByAuthorIdNot(userId, pageable)
                                                               .stream()
                                                               .collect(Collectors.toMap(ItemRequest::getId,
                                                                         Function.identity()));
        return enrichRequestsByItems(itemRequests);
    }

    @Override
    public ItemRequestDtoOutWithItems getRequestById(Long userId, Long requestId) {
        existsUserById(userId);

        Optional<ItemRequest> itemRequest = requestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            throw new EntityNotFoundException(requestId, ItemRequest.class);
        }

        return requestMapper.toDtoWithItems(itemRequest.get(),
                itemRepository.findByRequest_Id(requestId)
                              .stream()
                              .map(itemMapper::toDto)
                              .collect(Collectors.toList()));
    }

    private User findUserById(long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
    }

    private void existsUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(userId, User.class);
        }
    }

    private List<ItemRequestDtoOutWithItems> enrichRequestsByItems(Map<Long, ItemRequest> itemRequests) {
        Map<Long, List<ItemDto>> items = itemRepository.findByRequest_Ids(itemRequests.keySet())
                                                       .stream()
                                                       .map(itemMapper::toDto)
                                                       .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequests.entrySet()
                .stream()
                .map(f -> requestMapper.toDtoWithItems(f.getValue(),
                           (items.getOrDefault(f.getKey(), Collections.emptyList()))))
                .collect(Collectors.toList());
    }
}
