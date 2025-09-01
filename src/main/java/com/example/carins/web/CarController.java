package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/cars")
public class CarController {

    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2050;

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> insuranceValid(
            @PathVariable Long carId,
            @RequestParam String date
    ) {

        if (!service.carExists(carId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Car not found: " + carId));
        }


        final LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid date format. Use YYYY-MM-DD."));
        }


        int y = d.getYear();
        if (y < MIN_YEAR || y > MAX_YEAR) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Date out of supported range (" + MIN_YEAR + "–" + MAX_YEAR + ")."));
        }

        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    @PostMapping
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDto dto) {
        Car car = new Car();
        car.setVin(dto.vin());
        car.setMake(dto.make());
        car.setModel(dto.model());
        car.setYearOfManufacture(dto.year());

        try {
            Car saved = service.createCar(car);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
    public record ErrorResponse(String message) {}
}
