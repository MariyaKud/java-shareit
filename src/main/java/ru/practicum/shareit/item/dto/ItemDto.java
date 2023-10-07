package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
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
