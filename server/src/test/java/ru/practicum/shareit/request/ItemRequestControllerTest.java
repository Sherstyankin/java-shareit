package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;

    @BeforeEach
    void setUp() {

        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Нужна палатка")
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .description("Нужна палатка")
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(1L, itemRequestDto))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString())));
    }

    @Test
    void findAllRequestsByRequestor() throws Exception {
        when(itemRequestService.findAllRequestsByRequestor(1L))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto.getCreated().toString())));
    }

    @Test
    void findAllRequests() throws Exception {
        when(itemRequestService.findAllRequests(1L, 0, 10))
                .thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestResponseDto.getCreated().toString())));
    }

    @Test
    void findRequestById() throws Exception {
        when(itemRequestService.findRequestById(1L, 1L))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestResponseDto.getCreated().toString())));
    }
}