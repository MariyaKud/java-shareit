package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        findUserById(userId);
        itemDto.setId(null);
        final Item item = itemMapper.mapperItemFromDto(userId, itemDto);
        final Item newItem = itemRepository.save(item);
        return itemMapper.mapperItemToDto(newItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        findUserById(userId);

        final String name = itemDto.getName();
        final String description = itemDto.getDescription();
        final Item updateItem = findItemById(itemId);

        if (userId != updateItem.getOwnerId()) {
            throw new ItemBelongsAnotherOwner();
        }

        if (name != null && !name.isBlank()) {
            updateItem.setName(name);
        }
        if (description != null && !description.isBlank()) {
            updateItem.setDescription(description);
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(updateItem);

        return itemMapper.mapperItemToDto(updateItem);
    }

    @Override
    public ItemDto getItemByIdForUserId(long userId, long itemId) {
        findUserById(userId);
        return itemMapper.mapperItemToDto(findItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(itemMapper::mapperItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsForUserWithId(long userId, String text) {
        findUserById(userId);

        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findByAvailableTrueAndContainingText(text)
               .stream()
               .map(itemMapper::mapperItemToDto)
               .collect(Collectors.toList());
    }

    private void findUserById(long userId) {
        userRepository.findById(userId)
                       .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                              .orElseThrow(() -> new EntityNotFoundException(itemId, Item.class));
    }
}
