package org.copyria.carapp.mapper;

import org.copyria.carapp.api.rest.model.CarResponseDto;
import org.copyria.carapp.api.rest.model.CreateCarDto;
import org.copyria.carapp.model.Car;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CarMapper {
    CarResponseDto toCarResponseDto(Car car);
    Car toCar(CreateCarDto carDto);
}
