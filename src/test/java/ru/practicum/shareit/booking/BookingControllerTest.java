package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.validation.ContextShareIt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Booking controller")
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final LocalDateTime current = LocalDateTime.now().plusMinutes(5L);

    private final UserDtoShort userDtoShort = new UserDtoShort(1L, "name@mail.ru");

    private final ItemDtoShort itemDto = new ItemDtoShort(1L, "name");

    private final BookingDtoOut bookingDto = BookingDtoOut.builder()
                                                          .id(1L)
                                                          .start(current)
                                                          .end(current.plusSeconds(1L))
                                                          .status(StatusBooking.APPROVED)
                                                          .item(itemDto)
                                                          .booker(userDtoShort)
                                                          .build();

    @Test
    @DisplayName("should create booking")
    void should_Create_Booking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(), any()))
                .thenReturn(bookingDto);

        BookingDto dto = BookingDto.builder()
                                   .id(bookingDto.getId())
                                   .itemId(bookingDto.getItem().getId())
                                   .start(bookingDto.getStart())
                                   .end(bookingDto.getEnd())
                                   .build();

        mockMvc.perform(post("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, userDtoShort.getId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingDto.getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1)).createBooking(anyLong(), any(), any());
    }

    @Test
    @DisplayName("should approve booking")
    void should_Approved_Booking() throws Exception {
        when(bookingService.approvedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userDtoShort.getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.status", Matchers.is(bookingDto.getStatus().toString()), String.class));

        verify(bookingService, times(1)).approvedBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("should get booking by id")
    void should_Get_Booking_ById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(ContextShareIt.HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    @DisplayName("should get booking by booker id")
    void should_Get_Bookings_By_Booker_Id() throws Exception {
        when(bookingService.getBookingsByBookerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, userDtoShort.getId())
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    @DisplayName("should get booking for owner item id")
    void should_Get_Bookings_For_Item_Owner_Id() throws Exception {
        when(bookingService.getBookingsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(ContextShareIt.HEADER_USER_ID, userDtoShort.getId())
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }
}