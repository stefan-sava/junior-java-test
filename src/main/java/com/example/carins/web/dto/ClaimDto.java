package com.example.carins.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimDto {
    public static class CreateClaimRequest {
        @NotNull
        private LocalDate claimDate;

        @NotBlank
        @Size(max = 500)
        private String description;

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 12, fraction = 2)
        private BigDecimal amount;

        public LocalDate getClaimDate() { return claimDate; }
        public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public record ClaimResponse(
            Long id,
            Long carId,
            LocalDate claimDate,
            String description,
            BigDecimal amount
    ) { }
}
