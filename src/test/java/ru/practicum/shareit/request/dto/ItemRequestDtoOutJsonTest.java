package ru.practicum.shareit.request.dto;


import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Item request dto json out")
class ItemRequestDtoOutJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoOut> jacksonTester;

    private final LocalDateTime current = LocalDateTime.now();

    private final ItemRequestDtoOut dto = new ItemRequestDtoOut(1L,"description", current);

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<ItemRequestDtoOut> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = "{   \n" +
                "    \"id\": 1,\n" +
                "    \"description\" : \"description\",\n" +
                "    \"created\": \"" + current + "\"\n" +
                "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(dto.getId());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("description")
                .isEqualTo(dto.getDescription());
        AssertionsForClassTypes.assertThat(dto).extracting("created").isEqualTo(dto.getCreated());
    }
}