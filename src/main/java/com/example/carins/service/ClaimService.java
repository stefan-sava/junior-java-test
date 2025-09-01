package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.web.dto.ClaimDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
public class ClaimService {

    private final CarRepository carRepository;
    private final InsuranceClaimRepository claimRepository;

    public ClaimService(CarRepository carRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.claimRepository = claimRepository;
    }

    @Transactional
    public InsuranceClaim registerClaim(
            @NotNull Long carId,
            @NotNull LocalDate claimDate,
            @NotNull String description,
            @NotNull BigDecimal amount) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NoSuchElementException("Car not found: " + carId));

        return claimRepository.save(new InsuranceClaim(car, claimDate, description, amount));
    }

    public ClaimDto.ClaimResponse getClaim(
            @NotNull Long carId,
            @NotNull Long id) {
        InsuranceClaim insuranceClaim = claimRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Claim not found: " + id));
        if(!insuranceClaim.getCar().getId().equals(carId)) {
            throw new NoSuchElementException("Claim " + id + " not found for car with id " + carId);
        }
        return  new ClaimDto.ClaimResponse(
                insuranceClaim.getId(),
                insuranceClaim.getCar().getId(),
                insuranceClaim.getClaimDate(),
                insuranceClaim.getDescription(),
                insuranceClaim.getAmount());
    }

}
