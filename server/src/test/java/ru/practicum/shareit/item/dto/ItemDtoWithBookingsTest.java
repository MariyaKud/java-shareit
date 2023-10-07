package ru.practicum.shareit.item.dto;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOutShort;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@DisplayName("Item dto json with bookings")
class ItemDtoWithBookingsTest {

    @Autowired
    private JacksonTester<ItemDtoOutWithBookings> jacksonTester;

    private final LocalDateTime current = LocalDateTime.now();

    private final BookingDtoOutShort bookingDto = new BookingDtoOutShort(1L, 1L,
                                                                          current, current.plusDays(1));

    private final CommentDtoOut commentDto = new CommentDtoOut(1L,"text", "user",  current);

    private final ItemDtoOutWithBookings dto = ItemDtoOutWithBookings.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .lastBooking(bookingDto)
            .comments(Set.of(commentDto))
            .build();

    @Test
    @DisplayName("should serialize")
    void testSerialize() throws Exception {

        JsonContent<ItemDtoOutWithBookings> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .extracting("id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .extracting("bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .extracting("start").isEqualTo(current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    @DisplayName("should deserialize")
    void testDeserialize() throws IOException {
        final String dtoJson = "{   \n" +
                "    \"id\": 1,\n" +
                "    \"name\" : \"name\",\n" +
                "    \"description\" : \"description\",\n" +
                "    \"available\":true,\n" +
                "    \"lastBooking\":{\n\"id\": 1, \n\"bookerId\": 1, \n" +
                "    \"start\": \"" + current + "\",\n" +
                "    \"end\": \"" + current.plusDays(1) + "\"\n}" + ",\n" +
                "    \"comments\":[{\n" +
                "                   \"id\":1,\n" +
                "                   \"text\":\"text\",\n" +
                "                   \"authorName\":\"user\",\n" +
                "                   \"created\": \"" + current + "\"\n" +
                "                  }]" +
                "    \n}";

        var itemDto = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dto).extracting("id").isEqualTo(itemDto.getId());
        AssertionsForClassTypes.assertThat(dto).extracting("name").isEqualTo(itemDto.getName());
        AssertionsForClassTypes.assertThat(dto).extracting("description")
                                               .isEqualTo(itemDto.getDescription());
        AssertionsForClassTypes.assertThat(dto).extracting("available").isEqualTo(itemDto.getAvailable());
        AssertionsForClassTypes.assertThat(dto).extracting("lastBooking").extracting("id")
                                               .isEqualTo(itemDto.getLastBooking().getId());
    }
}