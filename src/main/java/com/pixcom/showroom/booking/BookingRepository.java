
package com.pixcom.showroom.booking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByRoomNumberAndStatus(
        Integer roomNumber,
        String status
    );

    boolean existsByRoomNumberAndStatus(
        Integer roomNumber,
        String status
    );

    List<Booking> findAllByOrderByCreatedAtDesc();
}
