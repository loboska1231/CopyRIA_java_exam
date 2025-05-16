package org.copyria.orderapp.repository;

import org.copyria.orderapp.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCity(String city);
    List<OrderEntity> findAllByCityAndPriceGreaterThanEqualAndPriceLessThanEqual(String city, double priceAfter, double priceBefore);
    List<OrderEntity> findAllByRegion(String region);
    List<OrderEntity> findAllByRegionAndPriceGreaterThanEqualAndPriceLessThanEqual(String city, double priceAfter, double priceBefore);
    List<OrderEntity> findAllByPriceGreaterThanEqualAndPriceLessThanEqual( double priceAfter, double priceBefore);
    List<OrderEntity> findAllByPriceLessThanEqual(double priceAfter);
    List<OrderEntity> findAllByPriceGreaterThanEqual(double priceBefore);

    OrderEntity findByCarId(String carId);
    default double avgPrice(List<OrderEntity> orders) {
        return orders.stream().mapToDouble(OrderEntity::getPrice).average().orElse(0.0);
    }

}
