package org.copyria.carapp.repository;

import org.copyria.carapp.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public @Repository interface CarRepository extends MongoRepository<Car, String> {
    List<Car> findAllByProducer(String producer);
    List<Car> findAllByYearGreaterThanEqual(Integer year);
    List<Car> findAllByYearLessThanEqual(Integer year);
    List<Car> findAllByYearBetween(Integer yearAfter, Integer yearBefore);
}
