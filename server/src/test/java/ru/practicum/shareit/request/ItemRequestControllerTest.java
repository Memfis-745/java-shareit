package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOne;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private RequestServiceImpl requestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto requestDto;
    private ItemRequestDtoOne requestDtoOne;
    static final String header = "X-Sharer-User-Id";

    @BeforeEach
    public void itemCreate() {
        requestDto = new ItemRequestDto(1L, "Описание запроса",
                LocalDateTime.now(), null);
        requestDtoOne = new ItemRequestDtoOne(1L, "Описание запроса", null,
                LocalDateTime.now(), null);
    }

    @Test
    void saveItemRequest() throws Exception {
        when(requestService.createItemRequest(anyLong(), any()))
                .thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", is(requestDto.getItems())));

        verify(requestService, times(1))
                .createItemRequest(anyLong(), any());
    }

    @Test
    void getItemRequests() throws Exception {

        when(requestService.findUserItemRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()));

        verify(requestService, times(1))
                .findUserItemRequests(anyLong());
    }

    @Test
    void getItemRequestsFromOtherUsers() throws Exception {
        int from = 0;
        int size = 5;
        List<ItemRequestDto> requestDtoList = List.of(requestDto);
        when(requestService.findRequestsAnotherUsers(anyLong(), anyInt(), anyInt()))
                .thenReturn(requestDtoList);

        mvc.perform(get("/requests/all")
                        .header(header, 1)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDtoList)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", notNullValue()));

        verify(requestService, times(1))
                .findRequestsAnotherUsers(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getOneItemRequest() throws Exception {

        when(requestService.findOneItemRequest(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOne.getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", is(requestDtoOne.getItems())));

        verify(requestService, times(1))
                .findOneItemRequest(anyLong(), anyLong());
    }
}