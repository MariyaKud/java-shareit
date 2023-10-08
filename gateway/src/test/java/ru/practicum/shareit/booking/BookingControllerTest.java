package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.ContextShareIt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Booking controller")
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingClient bookingClient;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final long firstId = 1L;

    private final LocalDateTime current = LocalDateTime.now().plusMinutes(5L);

    private final BookingDto bookingDto = new BookingDto(1L, current, current.plusDays(1));

    ResponseEntity<Object> response = new ResponseEntity<>(bookingDto, HttpStatus.OK);

    @Test
    @DisplayName("should create booking")
    void should_create_booking() throws Exception {
        when(bookingClient.bookItem(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(anyLong(), any());
    }

    @Test
    @DisplayName("should approve booking")
    void should_approved_booking() throws Exception {
        when(bookingClient.approvedBookItem(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", firstId)
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approvedBookItem(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("should get booking by id")
    void should_get_booking_by_id() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", firstId)
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingClient, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    @DisplayName("should get booking by booker id")
    void should_get_bookings_by_booker_id() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                       )
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should get booking for owner item id")
    void should_get_bookings_for_item_owner_id() throws Exception {
        when(bookingClient.getBookingsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsByOwnerId(anyLong(), any(), anyInt(), anyInt());
    }

    @DisplayName("should not validate item id negative")
    @Test
    public void should_not_validate_id() throws Exception {
        final BookingDto dto = new BookingDto(-1L, current, current.minusDays(1));

        mockMvc.perform(post("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(0)).bookItem(anyLong(), any());
    }

    @DisplayName("should not validate start in past")
    @Test
    public void should_not_validate_start_in_past() throws Exception {
        final BookingDto dto = new BookingDto(1L, current.minusDays(10), current.minusDays(1));

        mockMvc.perform(post("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(0)).bookItem(anyLong(), any());
    }

    @DisplayName("should not get bookings by user id")
    @Test
    void should_not_get_bookings_by_user_id() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("should not get bookings by owner id")
    @Test
    void should_not_get_bookings_by_owner_id() throws Exception {
        when(bookingClient.getBookingsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header(ContextShareIt.HEADER_USER_ID, firstId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isInternalServerError());
    }
}