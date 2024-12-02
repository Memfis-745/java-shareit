package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingRequestDto bookingDto;
    private ResponseEntity<Object> response;
    static final String header = "X-Sharer-User-Id";

    @BeforeEach
    public void itemCreate() {
        bookingDto = new BookingRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5),
                1L, BookingStatus.APPROVED);
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @Test
    void saveBooking() throws Exception {
        when(bookingClient.addBooking(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", notNullValue()))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .addBooking(anyLong(), any());
    }


    @Test
    void saveBookingWithStartIsNull() throws Exception {
        bookingDto.setStart(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .addBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithStartIsPast() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .addBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithEndIsNull() throws Exception {
        bookingDto.setEnd(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .addBooking(anyLong(), any());
    }

    @Test
    void saveBookingWithNullItemId() throws Exception {
        bookingDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never())
                .addBooking(anyLong(), any());
    }

    @Test
    void bookingApprove() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.approveBooking(userId, bookingId, approved))
                .thenReturn(response);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(header, userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .approveBooking(userId, bookingId, approved);
    }

    @Test
    void findBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingClient.getBookingId(userId, bookingId))
                .thenReturn(response);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingClient, times(1))
                .getBookingId(userId, bookingId);
    }


    @Test
    void findUserBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingRequestDto> bookingList = List.of(bookingDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(bookingList, HttpStatus.OK);

        when(bookingClient.getAllBookingByUser(userId, enumState, from, size))
                .thenReturn(responseWithList);

        mvc.perform(get("/bookings")
                        .header(header, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingClient, times(1))
                .getAllBookingByUser(userId, enumState, from, size);
    }

    @Test
    void findOwnerBookings() throws Exception {
        long userId = 1L;
        String state = "FUTURE";
        BookingState enumState = BookingState.FUTURE;
        int from = 0;
        int size = 5;
        List<BookingRequestDto> bookingList = List.of(bookingDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(bookingList, HttpStatus.OK);

        when(bookingClient.getAllBookingByOwner(userId, enumState, from, size))
                .thenReturn(responseWithList);

        mvc.perform(get("/bookings/owner")
                        .header(header, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(bookingList)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", notNullValue()))
                .andExpect(jsonPath("$[0].end", notNullValue()));

        verify(bookingClient, times(1))
                .getAllBookingByOwner(userId, enumState, from, size);
    }

}
