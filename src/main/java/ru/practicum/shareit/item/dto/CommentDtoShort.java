package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentDtoShort {
    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;
}
