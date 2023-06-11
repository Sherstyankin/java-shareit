package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item i " +
            "join i.owner o " +
            "where o.id = ?1")
    List<Item> findAllByUserId(Long userId);

    @Query(value = "select * " +
            "from items as i " +
            "where (i.item_name ilike %?1% or i.description ilike %?1%) " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByText(String text);
}