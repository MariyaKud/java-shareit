package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        final PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return userService.getAllUsers(pageable);
    }

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                               @Validated(Update.class) @RequestBody UserDto user) {
         return userService.updateUser(userId, user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
}
