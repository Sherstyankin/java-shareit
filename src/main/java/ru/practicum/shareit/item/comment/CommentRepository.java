package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c " +
            "from Comment c " +
            "join c.item i " +
            "where i.id = ?1")
    List<Comment> findAllByItemId(Long itemId);
}
