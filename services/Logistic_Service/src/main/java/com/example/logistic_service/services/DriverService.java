package com.example.logistic_service.services;

import com.example.logistic_service.entity.Driver;
import com.example.logistic_service.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverService {
    @Autowired private DriverRepository driverRepository;

    public Driver addDriver(Driver driver) {
        if (driverRepository.existsByVehiclePlate(driver.getVehiclePlate())) {
            throw new RuntimeException("Xe này đã tồn tại trong hệ thống!");
        }
        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
}