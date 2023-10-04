package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Booking dto json out")
class BookingDtoOutJsonTest {

    @Autowired
    private JacksonTester<BookingDtoOut> jacksonTester;

    private final LocalDateTime current = LocalDateTime.now();

    private final ItemDtoShort itemDto = new ItemDtoShort(1L, "item1");

    private final UserDtoShort userDto = new UserDtoShort(1L, "user@mail.ru");

    private final BookingDtoOut bookingDto = BookingDtoOut.builder()
                                                          .id(1L)
                                                          .start(current)
                                                          .end(current.plusMinutes(1))
                                                          .status(StatusBooking.APPROVED)
                                                          .booker(userDto)
                                                          .item(itemDto)
                                                          .build();

    private final String bookingDtoJson = "{   \n" +
            "    \"id\": 1,\n" +
            "    \"start\": \"" + current + "\",\n" +
            "    \"end\": \"" + current.plusMinutes(1) + "\",\n" +
            "    \"status\":\"APPROVED\",\n" +
            "    \"item\":{\n\"id\": 1,\n\"name\":\"item1\"\n}" + ",\n" +
            "    \"booker\":{\n\"id\":1,\n\"email\":\"user@mail.ru\"\n}" +
            "    \n}";

    @Test
    @DisplayName("should serialize")
    void test_serialize() throws Exception {

        JsonContent<BookingDtoOut> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(current.plusMinutes(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(StatusBooking.APPROVED.toString());
        assertThat(result).extractingJsonPathValue("$.item")
                          .extracting("id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item")
                .extracting("name").isEqualTo("item1");
        assertThat(result).extractingJsonPathValue("$.booker")
                .extracting("id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.booker")
                .extracting("email").isEqualTo("user@mail.ru");
    }

    @Test
    @DisplayName("should deserialize")
    void test_deserialize() throws IOException {
        var dto = jacksonTester.parseObject(bookingDtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(bookingDto.getId());
        AssertionsForClassTypes.assertThat(dto).extracting("start").isEqualTo(bookingDto.getStart());
        AssertionsForClassTypes.assertThat(dto).extracting("end").isEqualTo(bookingDto.getEnd());
        AssertionsForClassTypes.assertThat(dto).extracting("status").isEqualTo(bookingDto.getStatus());
        AssertionsForClassTypes.assertThat(dto).extracting("item").isEqualTo(bookingDto.getItem());
        AssertionsForClassTypes.assertThat(dto).extracting("booker").isEqualTo(bookingDto.getBooker());
    }
}