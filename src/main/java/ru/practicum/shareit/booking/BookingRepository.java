package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 and (CURRENT_TIMESTAMP between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findCurrentBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 and CURRENT_TIMESTAMP > b.end " +
            "order by b.start desc")
    List<Booking> findPastBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 and CURRENT_TIMESTAMP < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findRejectedBookingByUserId(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and (CURRENT_TIMESTAMP between b.start and b.end) " +
            "order by b.start desc")
    List<Booking> findCurrentBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and CURRENT_TIMESTAMP > b.end " +
            "order by b.start desc")
    List<Booking> findPastBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and CURRENT_TIMESTAMP < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and b.status = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findRejectedBookingByOwnerItems(Long userId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 and b.start < CURRENT_TIMESTAMP " +
            "order by b.end desc")
    List<Booking> findLastBookingForItem(Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 and b.start > CURRENT_TIMESTAMP " +
            "order by b.end asc")
    List<Booking> findNextBookingForItem(Long itemId);

    @Query(value = "select case when exists (select * " +
            "from bookings as b " +
            "where b.user_id = ?1 and b.item_id = ?2 and CURRENT_TIMESTAMP > b.end_time) " +
            "then cast(1 as bit) " +
            "else cast(0 as bit) end", nativeQuery = true)
    Boolean checkIsBookerAndFinished(Long userId, Long itemId);
}
