package org.copyria.carapp.services;

import lombok.RequiredArgsConstructor;
import org.copyria.carapp.api.event.model.CarDeletedEventPayload;
import org.copyria.carapp.api.event.producer.ICarEventsProducer;
import org.copyria.carapp.api.rest.model.CarResponseDto;
import org.copyria.carapp.api.rest.model.CreateCarDto;
import org.copyria.carapp.mapper.CarMapper;
import org.copyria.carapp.model.Car;
import org.copyria.carapp.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final ICarEventsProducer carEventsProducer;
    public List<CarResponseDto> getCars(Integer yearAfter, Integer yearBefore){
        List<Car> cars;
        if(yearAfter != null && yearBefore != null){
            cars = carRepository.findAllByYearBetween(yearAfter, yearBefore);
        } else if (yearAfter != null){
            cars = carRepository.findAllByYearGreaterThanEqual(yearAfter);
        }else if (yearBefore != null){
            cars = carRepository.findAllByYearLessThanEqual(yearBefore);
        }
        else {
            cars = carRepository.findAll();
        }
        return cars.stream()
                .map(carMapper::toCarResponseDto)
                .toList();
    }
    public CarResponseDto createCar(CreateCarDto carDto){
        Car car = carMapper.toCar(carDto);
        var saved = carRepository.save(car);
        return carMapper.toCarResponseDto(saved);
    }
    public void deleteCar(String id){
        carRepository.deleteById(id);
        CarDeletedEventPayload eventPayload = new CarDeletedEventPayload().withCarId(id);
        carEventsProducer.onCarDeletedEvent(eventPayload);
        //event to cloud-> orderService order : status=Cancelled
    }

    public CarResponseDto getCar(String id) {
        return carMapper.toCarResponseDto(carRepository.findById(id).get());
    }
}
