package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = :userId and (CURRENT_TIMESTAMP between b.start and b.end)")
    List<Booking> findCurrentBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = :userId and CURRENT_TIMESTAMP > b.end")
    List<Booking> findPastBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = :userId and CURRENT_TIMESTAMP < b.start")
    List<Booking> findFutureBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = :userId and b.status = 'WAITING'")
    List<Booking> findWaitingBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.booker br " +
            "where br.id = :userId and b.status = 'REJECTED'")
    List<Booking> findRejectedBookingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId")
    List<Booking> findAllBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and (CURRENT_TIMESTAMP between b.start and b.end)")
    List<Booking> findCurrentBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and CURRENT_TIMESTAMP > b.end")
    List<Booking> findPastBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and CURRENT_TIMESTAMP < b.start")
    List<Booking> findFutureBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and b.status = 'WAITING'")
    List<Booking> findWaitingBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and b.status = 'REJECTED'")
    List<Booking> findRejectedBookingByOwnerItems(@Param("userId") Long userId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId and b.start < CURRENT_TIMESTAMP " +
            "order by b.end desc")
    List<Booking> findLastBookingForItem(@Param("itemId") Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId and b.start > CURRENT_TIMESTAMP " +
            "order by b.end asc")
    List<Booking> findNextBookingForItem(@Param("itemId") Long itemId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and b.start < CURRENT_TIMESTAMP " +
            "order by b.end desc")
    List<Booking> findLastBookingForAllOwnerItems(@Param("userId") Long userId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = :userId and b.start > CURRENT_TIMESTAMP " +
            "order by b.end asc")
    List<Booking> findNextBookingForAllOwnerItems(@Param("userId") Long userId);

    @Query(value = "select case when exists (select * " +
            "from bookings as b " +
            "where b.user_id = :userId and b.item_id = :itemId and CURRENT_TIMESTAMP > b.end_time) " +
            "then cast(1 as bit) " +
            "else cast(0 as bit) end", nativeQuery = true)
    Boolean checkIsBookerAndFinished(@Param("userId") Long userId,
                                     @Param("itemId") Long itemId);
}
