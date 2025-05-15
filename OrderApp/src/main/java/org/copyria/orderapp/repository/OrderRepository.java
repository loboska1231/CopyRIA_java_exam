package org.copyria.orderapp.repository;

import org.copyria.orderapp.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCity(String city);
    List<OrderEntity> findAllByCityAndPriceGreaterThanEqualAndPriceLessThanEqual(String city, Long priceAfter, Long priceBefore);
    List<OrderEntity> findAllByRegion(String region);
    List<OrderEntity> findAllByRegionAndPriceGreaterThanEqualAndPriceLessThanEqual(String city, Long priceAfter, Long priceBefore);
    List<OrderEntity> findAllByPriceGreaterThanEqualAndPriceLessThanEqual( Long priceAfter, Long priceBefore);
    List<OrderEntity> findAllByPriceLessThanEqual(Long priceAfter);
    List<OrderEntity> findAllByPriceGreaterThanEqual(Long priceBefore);

    @Query("select avg(o.price) from OrderEntity o where o.city =: city")
    Long findAveragePriceByCity(String city);
    @Query("select avg(o.price) from OrderEntity o where o.region =: region")
    Long findAveragePriceByRegion(String region);
    @Query("select avg(o.price) from OrderEntity o")
    Long findAveragePrice();

    OrderEntity findByCarId(String carId);
}
