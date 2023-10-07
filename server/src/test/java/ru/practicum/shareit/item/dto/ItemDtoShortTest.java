package ru.practicum.shareit.item.dto;

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
@DisplayName("Item dto short json")
class ItemDtoShortTest {
    @Autowired
    private JacksonTester<ItemDtoOut> jacksonTester;

    private final ItemDtoOut dto = new ItemDtoOut(1L,"text");

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<ItemDtoOut> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("text");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = "{   \n" +
                               "    \"id\": 1,\n" +
                               "    \"name\" : \"text\"\n" +
                               "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dtoTest).extracting("id").isEqualTo(dto.getId());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("name").isEqualTo(dto.getName());
    }
}