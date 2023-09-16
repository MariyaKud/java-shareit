package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;
import ru.practicum.shareit.validation.ContextShareIt;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                               @Validated(Create.class) @RequestBody ItemDto item) {
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                               @PathVariable Long itemId,
                                @Validated(Update.class) @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookings getItemByIdForUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                                  @PathVariable Long itemId) {
        return itemService.getItemByIdForUserId(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookings> getItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByUserId(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                              @RequestParam String text) {
        return itemService.searchItemsForUserWithId(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(ContextShareIt.HEADER_USER_ID) Long userId,
                                     @PathVariable Long itemId,
                                      @RequestBody @Valid CommentDtoShort commentDto) {
        return itemService.createComment(userId, itemId, commentDto, LocalDateTime.now());
    }
}
