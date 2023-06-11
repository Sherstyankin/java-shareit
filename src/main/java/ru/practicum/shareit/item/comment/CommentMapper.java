package ru.practicum.shareit.item.comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    private CommentMapper() {
    }

    public static ResponseCommentDto mapToResponseCommentDto(Comment comment) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<ResponseCommentDto> mapToResponseCommentDto(Iterable<Comment> comments) {
        List<ResponseCommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToResponseCommentDto(comment));
        }
        return dtos;
    }
}
