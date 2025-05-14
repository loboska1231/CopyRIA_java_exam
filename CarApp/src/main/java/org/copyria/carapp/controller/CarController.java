package org.copyria.carapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.copyria.carapp.api.rest.controller.CarApi;
import org.copyria.carapp.api.rest.model.CarResponseDto;
import org.copyria.carapp.api.rest.model.CreateCarDto;
import org.copyria.carapp.model.Car;
import org.copyria.carapp.services.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CarController implements CarApi {
    private final CarService carService;

    @Override
    public ResponseEntity<Void> deleteCar(String id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<CarResponseDto>> getCars(Integer yearAfter, Integer yearBefore) {
        return ResponseEntity.ok(carService.getCars(yearAfter,yearBefore));
    }

    @Override
    public ResponseEntity<CarResponseDto> postCar(CreateCarDto createCarDto) {
        return ResponseEntity.ok(carService.createCar(createCarDto));
    }
}
