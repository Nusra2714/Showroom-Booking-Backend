
package com.pixcom.showroom.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class BookingController {

    private static final String ACTIVE = "ACTIVE";
    private static final String RELEASED = "RELEASED";

    private final BookingRepository repository;

    public BookingController(BookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/rooms")
    public List<RoomStatus> getRooms() {
        return IntStream.rangeClosed(1, 4)
            .mapToObj(number ->
                repository.findByRoomNumberAndStatus(number, ACTIVE)
                    .map(booking -> new RoomStatus(number, "BUSY", booking))
                    .orElseGet(() ->
                        new RoomStatus(number, "AVAILABLE", null)
                    )
            )
            .toList();
    }

    @GetMapping("/bookings")
    public List<Booking> getBookings() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(
        @Valid @RequestBody BookingRequest request
    ) {
        if (request.roomNumber() < 1 || request.roomNumber() > 4) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Room number must be between 1 and 4"
            );
        }

        if (repository.existsByRoomNumberAndStatus(
                request.roomNumber(), ACTIVE)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This room is already booked. Release it before booking again."
            );
        }

        Booking booking = new Booking();
        booking.setRoomNumber(request.roomNumber());
        booking.setCustomerName(request.customerName().trim());
        booking.setMobile(request.mobile().trim());
        booking.setEmail(request.email().trim());
        booking.setModel(request.model().trim());
        booking.setDate(request.date());
        booking.setTime(request.time());
        booking.setStatus(ACTIVE);

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.saveAndFlush(booking));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "The booking could not be saved. Refresh room availability."
            );
        }
    }

    @DeleteMapping("/rooms/{roomNumber}/booking")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void releaseRoom(@PathVariable int roomNumber) {
        if (roomNumber < 1 || roomNumber > 4) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Room number must be between 1 and 4"
            );
        }

        Booking booking = repository
            .findByRoomNumberAndStatus(roomNumber, ACTIVE)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room is already available"
            ));

        booking.setStatus(RELEASED);
        booking.setReleasedAt(LocalDateTime.now());

        repository.save(booking);
    }
}
