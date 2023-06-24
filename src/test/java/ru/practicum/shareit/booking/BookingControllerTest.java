package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.validation.ValidationService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private BookingController bookingController;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private BookingDto bookingDto;
    private ResponseBookingDto responseBookingDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Рюкзак")
                .description("Походный")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Сергей")
                .email("sher@mail.com")
                .build();

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.of(2023, 6, 29, 12, 23, 23))
                .end(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .itemId(1L)
                .build();

        responseBookingDto = ResponseBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 6, 29, 12, 23, 23))
                .end(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(1L, bookingDto))
                .thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(responseBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(responseBookingDto.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBookingDto.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void changeStatus() throws Exception {
        responseBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseBookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(responseBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(responseBookingDto.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBookingDto.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void findBookingInfo() throws Exception {
        when(bookingService.findBookingInfo(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(responseBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(responseBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(responseBookingDto.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseBookingDto.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void findAllBookingByUserId() throws Exception {
        when(bookingService.findAllBookingByUserId(1L, BookingState.ALL, 0, 10))
                .thenReturn(List.of(responseBookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(responseBookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(responseBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(responseBookingDto.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(responseBookingDto.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void findAllBookingByOwnerItems() throws Exception {
        when(bookingService.findAllBookingByOwnerItems(1L, BookingState.ALL, 0, 10))
                .thenReturn(List.of(responseBookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(responseBookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(responseBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(responseBookingDto.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(responseBookingDto.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].status", is(responseBookingDto.getStatus().toString())));
    }
}