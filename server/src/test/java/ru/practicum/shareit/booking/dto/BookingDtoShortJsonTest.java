package ru.practicum.shareit.booking.dto;

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
@DisplayName("Booking dto json short")
class BookingDtoShortJsonTest {

    @Autowired
    private JacksonTester<BookingDtoOutShort> jacksonTester;

    private final LocalDateTime current = LocalDateTime.now();

    private final BookingDtoOutShort bookingDto = new BookingDtoOutShort(1L, 1L,  current, current.plusDays(1));

    private final String bookingDtoJson = "{   \n" +
            "    \"id\": 1,\n" +
            "    \"bookerId\": 1,\n" +
            "    \"start\": \"" + current + "\",\n" +
            "    \"end\": \"" + current.plusDays(1) + "\"\n" +
            "}";

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {

        JsonContent<BookingDtoOutShort> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(current.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        var dto = jacksonTester.parseObject(bookingDtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(bookingDto.getId());
        AssertionsForClassTypes.assertThat(dto).extracting("bookerId").isEqualTo(bookingDto.getBookerId());
        AssertionsForClassTypes.assertThat(dto).extracting("start").isEqualTo(bookingDto.getStart());
        AssertionsForClassTypes.assertThat(dto).extracting("end").isEqualTo(bookingDto.getEnd());
    }
}