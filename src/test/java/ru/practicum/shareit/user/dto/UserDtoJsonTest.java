package ru.practicum.shareit.user.dto;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private JacksonTester<UserDtoShort> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDtoShort userDto = new UserDtoShort(1L, "john.doe@mail.com");

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {

        JsonContent<UserDtoShort> result = jacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        final String userDtoJson = objectMapper.writeValueAsString(userDto);

        var dto = jacksonTester.parseObject(userDtoJson);

        assertThat(dto).extracting("id").isEqualTo(userDto.getId());
        assertThat(dto).extracting("email").isEqualTo(userDto.getEmail());
    }
}