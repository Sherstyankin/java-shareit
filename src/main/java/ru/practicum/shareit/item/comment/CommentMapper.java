package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {
    public ResponseCommentDto mapToResponseCommentDto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<ResponseCommentDto> mapToResponseCommentDto(Iterable<Comment> comments) {
        List<ResponseCommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToResponseCommentDto(comment));
        }
        return dtos;
    }
}
