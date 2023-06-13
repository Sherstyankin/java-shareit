package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query(value = "select * " +
            "from items as i " +
            "where (i.item_name ilike %:text% or i.description ilike %:text%) " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByText(@Param("text") String text);
}