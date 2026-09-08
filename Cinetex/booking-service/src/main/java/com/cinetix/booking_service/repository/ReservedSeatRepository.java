package com.cinetix.booking_service.repository;

import com.cinetix.booking_service.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    List<ReservedSeat> findByShowtimeId(Long showtimeId);

    @Query("select rs.seatLabel from ReservedSeat rs where rs.showtimeId = :showtimeId and rs.seatLabel in :labels")
    List<String> findBookedSeatLabels(@Param("showtimeId") Long showtimeId, @Param("labels") Collection<String> labels);
}

