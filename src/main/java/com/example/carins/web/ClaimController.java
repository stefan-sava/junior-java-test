package com.example.carins.web;

import com.example.carins.model.InsuranceClaim;
import com.example.carins.service.ClaimService;
import com.example.carins.web.dto.ClaimDto.CreateClaimRequest;
import com.example.carins.web.dto.ClaimDto.ClaimResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cars/{carId}/claims")
@Validated
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<?> createClaim(
            @PathVariable Long carId,
            @Valid @RequestBody CreateClaimRequest body) {

        try {
            InsuranceClaim saved = claimService.registerClaim(
                    carId, body.getClaimDate(), body.getDescription(), body.getAmount());

            ClaimResponse dto = new ClaimResponse(
                    saved.getId(),
                    carId,
                    saved.getClaimDate(),
                    saved.getDescription(),
                    saved.getAmount()
            );

            URI location = URI.create("/api/cars/" + carId + "/claims/" + saved.getId());
            return ResponseEntity.created(location).body(dto);

        } catch (java.util.NoSuchElementException notFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(notFound.getMessage()));
        } catch (IllegalArgumentException bad) {
            return ResponseEntity.badRequest().body(new ErrorResponse(bad.getMessage()));
        }
    }

    public record ErrorResponse(String message) {}
}
