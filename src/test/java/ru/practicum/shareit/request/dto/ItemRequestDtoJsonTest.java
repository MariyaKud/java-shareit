package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Item request dto json in")
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    private final ItemRequestDto dto = new ItemRequestDto("description");

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<ItemRequestDto> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = "{\n" +
                               "  \"description\" : \"description\"\n" +
                               "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        assertThat(dtoTest).extracting("description").isEqualTo(dto.getDescription());
    }
}