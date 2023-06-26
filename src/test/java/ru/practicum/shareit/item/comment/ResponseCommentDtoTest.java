package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ResponseCommentDtoTest {
    @Autowired
    private JacksonTester<ResponseCommentDto> json;

    @Test
    void testResponseCommentDto() throws Exception {
        ResponseCommentDto dto = ResponseCommentDto.builder()
                .id(1L)
                .authorName("Сергей")
                .text("Удобный рюкзак")
                .created(LocalDateTime.of(2023, 6, 30, 12, 23, 23))
                .build();

        JsonContent<ResponseCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Сергей");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Удобный рюкзак");
        assertThat(result).extractingJsonPathValue("$.created", LocalDateTime.class)
                .isEqualTo(LocalDateTime.of(2023, 6, 30, 12, 23, 23)
                        .toString());
    }
}