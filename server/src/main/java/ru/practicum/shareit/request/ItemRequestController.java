package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.common.ContextShareIt;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut createItemRequest(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                               @RequestBody ItemRequestDtoIn itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDtoOutWithItems> getMyItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID)
                                                                  Long userId) {
        return itemRequestService.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutWithItems> getAllItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutWithItems getItemByIdForUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                           @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
