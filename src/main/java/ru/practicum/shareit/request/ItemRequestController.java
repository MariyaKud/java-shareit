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
import ru.practicum.shareit.request.dto.itemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.ContextShareIt;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public List<itemRequestDtoWithItems> getMyItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId) {
        return itemRequestService.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public List<itemRequestDtoWithItems> getAllItemRequests(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                                            @RequestParam(defaultValue = "10") @Min(0) int size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public itemRequestDtoWithItems getItemByIdForUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                        @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
