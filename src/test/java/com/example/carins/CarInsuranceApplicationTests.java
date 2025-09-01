package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.Owner;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.OwnerRepository;
import com.example.carins.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Autowired
    CarRepository carRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }


    @Test
    void insuranceValidityInclusiveEdges() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-01-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-12-31")));
        assertFalse(service.isInsuranceValid(1L, LocalDate.parse("2023-12-31")));
        assertFalse(service.isInsuranceValid(1L, LocalDate.parse("2026-01-02")));
    }

    @Test
    void insuranceValidityThrowsForMissingCar() {
        assertThrows(NoSuchElementException.class,
                () -> service.isInsuranceValid(9999L, LocalDate.parse("2025-06-01")));
    }


    @Test
    void insuranceValidityWithNullDateReturnsFalse() {
        assertFalse(service.isInsuranceValid(1L, null));
    }

    @Test
    void carExistsChecks() {
        assertTrue(service.carExists(1L));
        assertFalse(service.carExists(9999L));
    }

}
