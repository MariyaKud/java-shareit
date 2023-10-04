package ru.practicum.shareit.user.dto;

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
@DisplayName("User dto json shot")
class UserDtoShortJsonTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDto userDto = new UserDto(1L, "John", "john.doe@mail.com");

    @Test
    @DisplayName("should serialize")
    void testSerialize() throws Exception {

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    @DisplayName("should deserialize")
    void testDeserialize() throws IOException {
        final String userDtoJson = objectMapper.writeValueAsString(userDto);

        var dto = jacksonTester.parseObject(userDtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(userDto.getId());
        AssertionsForClassTypes.assertThat(dto).extracting("name").isEqualTo(userDto.getName());
        AssertionsForClassTypes.assertThat(dto).extracting("email").isEqualTo(userDto.getEmail());
    }
}