package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderByIdAsc(Long userId);

    List<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query(value = "select * " +
            "from items as i " +
            "where (i.item_name ilike %:text% or i.description ilike %:text%) " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByText(@Param("text") String text);

    @Query(value = "select * " +
            "from items as i " +
            "where (i.item_name ilike %:text% or i.description ilike %:text%) " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByText(@Param("text") String text, Pageable pageable);

    List<Item> findByItemRequestIn(List<ItemRequest> requests, Sort sort);

    List<Item> findByItemRequest(ItemRequest request, Sort sort);
}