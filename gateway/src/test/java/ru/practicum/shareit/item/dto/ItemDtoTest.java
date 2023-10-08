package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Item dto json test")
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final ItemDto dto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<ItemDto> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(true);
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = objectMapper.writeValueAsString(dto);

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dtoTest).extracting("id").isEqualTo(dto.getId());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("name").isEqualTo(dto.getName());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("description")
                .isEqualTo(dto.getDescription());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("available").isEqualTo(dto.getAvailable());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("requestId").isEqualTo(dto.getRequestId());
    }
}