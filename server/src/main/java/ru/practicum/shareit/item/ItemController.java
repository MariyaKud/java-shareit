package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.common.ContextShareIt;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                              @RequestBody ItemDto item) {
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOutWithBookings getItemByIdForUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                       @PathVariable Long itemId) {
        return itemService.getItemByIdForUserId(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOutWithBookings> getItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                             @RequestParam String text,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.searchItemsForUserWithId(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDtoIn commentDto) {
        return itemService.createComment(userId, itemId, commentDto, LocalDateTime.now());
    }
}
