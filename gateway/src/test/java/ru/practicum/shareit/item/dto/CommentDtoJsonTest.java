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
@DisplayName("Comment dto json in")
class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final CommentDto dto = new CommentDto("text");

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {
        JsonContent<CommentDto> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String dtoJson = objectMapper.writeValueAsString(dto);

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dtoTest).extracting("text").isEqualTo(dto.getText());
    }
}