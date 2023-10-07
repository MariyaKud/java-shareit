package ru.practicum.shareit.user.dto;

import lombok.*;

import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Positive(groups = {Create.class, Update.class})
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(min = 1, max = 255)
    private String name;
    @Size(min = 1, max = 512)
    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}
