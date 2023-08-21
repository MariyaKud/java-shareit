package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exeption.ItemBelongsAnotherOwner;
import ru.practicum.shareit.item.exeption.ItemNotFoundById;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exeption.UserNotFoundById;
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
        Item item = itemMapper.mapperItemFromDto(userId, itemRepository.getId(), itemDto);
        itemRepository.save(item);
        return itemMapper.mapperItemToDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        findUserById(userId);
        Item findItem = findItemById(itemId);

        if (userId != findItem.getOwnerId()) {
            throw new ItemBelongsAnotherOwner();
        }
        if (itemDto.getName() == null) {
            itemDto.setName(findItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(findItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(findItem.getAvailable());
        }

        Item item = itemMapper.mapperItemFromDto(userId, itemId, itemDto);
        itemRepository.save(item);
        return itemMapper.mapperItemToDto(item);
    }

    @Override
    public ItemDto getItemByIdForUserId(long userId, long itemId) {
        findUserById(userId);
        return itemMapper.mapperItemToDto(itemRepository.findItemByUserId(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.findItemsByUserId(userId)
                             .stream()
                             .map(itemMapper::mapperItemToDto)
                             .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByUserId(long userId, String text) {
        return itemRepository.findItemsByUserId(userId, text)
                             .stream()
                             .map(itemMapper::mapperItemToDto)
                             .collect(Collectors.toList());
    }

    private void findUserById(long userId) {
        final User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundById(userId);
        }
    }

    private Item findItemById(long itemId) {
        final Item item = itemRepository.findItemByUserId(itemId);
        if (item == null) {
            throw new ItemNotFoundById(itemId);
        }
        return item;
    }
}
