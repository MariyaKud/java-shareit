package ru.practicum.shareit.request.dto;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Item request dto json with item")
class ItemRequestDtoWithItemsJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoOutWithItems> jacksonTester;

    private final LocalDateTime current = LocalDateTime.now();

    private final ItemDto itemDto = new ItemDto(1L, "name", "description",
                                                 true, 1L);

    private final List<ItemDto> items = List.of(itemDto);

    private final ItemRequestDtoOutWithItems dto = new ItemRequestDtoOutWithItems(1L,"description",
                                                                                     current, items);

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<ItemRequestDtoOutWithItems> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = "{   \n" +
                "    \"id\": 1,\n" +
                "    \"description\" : \"description\",\n" +
                "    \"created\": \"" + current + "\",\n" +
                "    \"items\":[\n{" +
                "    \"id\":1,\n" +
                "    \"name\":\"name\",\n" +
                "    \"description\":\"description\",\n" +
                "    \"available\":true,\n" +
                "    \"requestId\":1 \n" +
                "                  }\n]\n" +
                "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(dto.getId());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("description")
                .isEqualTo(dto.getDescription());
        AssertionsForClassTypes.assertThat(dto).extracting("created").isEqualTo(dto.getCreated());
        AssertionsForClassTypes.assertThat(dto).extracting("items").isEqualTo(dto.getItems());
    }
}