package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Positive(groups = {Create.class, Update.class})
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}
