package ru.practicum.shareit.user.dto;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("User dto json")
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    private final UserDto userDto = new UserDto(1L, "John", "john.doe@mail.com");
    private final String userDtoJson = "{\n" +
            "    \"id\": 1,\n" +
            "    \"name\": \"John\",\n" +
            "    \"email\": \"john.doe@mail.com\"\n" +
            "}";

    @Test
    @DisplayName("should serialize")
    void testUserDto() throws Exception {

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    @DisplayName("should deserialize")
    void testDeserialize() throws IOException {
        var dto = jacksonTester.parseObject(userDtoJson);

        assertThat(dto).extracting("id").isEqualTo(userDto.getId());
        assertThat(dto).extracting("name").isEqualTo(userDto.getName());
        assertThat(dto).extracting("email").isEqualTo(userDto.getEmail());
    }
}