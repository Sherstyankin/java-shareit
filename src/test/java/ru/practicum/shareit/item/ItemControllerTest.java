package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private ItemDto itemDto;
    private ResponseItemDto responseItemDto;
    private RequestCommentDto requestCommentDto;
    private ResponseCommentDto responseCommentDto;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Рюкзак")
                .description("Походный")
                .available(true)
                .build();

        responseItemDto = ResponseItemDto.builder()
                .id(1L)
                .name("Рюкзак")
                .description("Походный")
                .available(true)
                .build();

        requestCommentDto = RequestCommentDto.builder()
                .text("Удобный рюкзак")
                .build();

        responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .authorName("Сергей")
                .text("Удобный рюкзак")
                .created(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .build();

    }

    @Test
    void findAllOwnerItems() throws Exception {
        when(itemService.findAllOwnerItems(1L, 0, 10))
                .thenReturn(List.of(responseItemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(responseItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void findById() throws Exception {
        when(itemService.findById(1L, 1L))
                .thenReturn(responseItemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(responseItemDto.getAvailable()), Boolean.class));
    }

    @Test
    void findByText() throws Exception {
        when(itemService.findByText("Рюкзак", 0, 10))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=Рюкзак"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class));
    }


    @Test
    void create() throws Exception {
        when(itemService.create(1L, itemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(1L, itemDto, 1L))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(1L, requestCommentDto, 1L))
                .thenReturn(responseCommentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText())))
                .andExpect(jsonPath("$.created", is(responseCommentDto.getCreated().toString())));
    }
}