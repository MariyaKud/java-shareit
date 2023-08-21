package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class User {
    @Positive
    private final Long id;
    @NotBlank
    private String name;
    @NotEmpty
    @Email
    @NotNull
    private String email;
}
