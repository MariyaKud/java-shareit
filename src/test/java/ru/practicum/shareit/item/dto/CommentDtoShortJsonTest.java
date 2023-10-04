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
@DisplayName("Comment dto json only text")
class CommentDtoShortJsonTest {
    @Autowired
    private JacksonTester<CommentDtoShort> jacksonTester;

    private final CommentDtoShort dto = new CommentDtoShort("text");

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<CommentDtoShort> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = "{\n" +
                               "  \"text\" : \"text\"\n" +
                               "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dtoTest).extracting("text").isEqualTo(dto.getText());
    }
}