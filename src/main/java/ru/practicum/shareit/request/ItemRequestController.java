package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.common.ContextShareIt;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut createItemRequest(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getMyItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId) {
        return itemRequestService.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getItemByIdForUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                        @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
