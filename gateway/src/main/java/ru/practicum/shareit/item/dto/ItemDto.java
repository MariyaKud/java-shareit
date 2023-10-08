package ru.practicum.shareit.item.dto;

import lombok.*;

import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    @Positive(groups = Update.class)
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(min = 1, max = 100, groups = {Update.class,Create.class})
    private String name;
    @NotBlank(groups = Create.class)
    @Size(min = 1, max = 1000, groups = {Update.class,Create.class})
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;
}
