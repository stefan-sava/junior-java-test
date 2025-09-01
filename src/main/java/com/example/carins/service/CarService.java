package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean carExists(Long carId) {
        return carRepository.existsById(carId);
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        if (!carRepository.existsById(carId)) {
            throw new java.util.NoSuchElementException("Car not found: " + carId);
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public Car createCar(Car car) {
        if (carRepository.existsByVin(car.getVin())) {
            throw new IllegalArgumentException("VIN already exists: " + car.getVin());
        }
        return carRepository.save(car);
    }


}
