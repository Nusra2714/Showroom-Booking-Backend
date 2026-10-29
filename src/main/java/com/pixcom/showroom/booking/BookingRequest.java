package com.pixcom.showroom.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(
        @NotNull Integer roomNumber,
        @NotBlank @Size(max = 120) String customerName,
        @NotBlank @Pattern(regexp = "[0-9+()\\- ]{7,20}", message = "must be a valid phone number") String mobile,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 100) String model,
        @NotNull LocalDate date,
        @NotNull LocalTime time
) {}
