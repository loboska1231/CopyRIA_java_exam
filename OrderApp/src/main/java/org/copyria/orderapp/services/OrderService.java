package org.copyria.orderapp.services;

import lombok.RequiredArgsConstructor;
import org.copyria.orderapp.client.rest.api.CarApi;
import org.copyria.orderapp.client.rest.model.*;
import org.copyria.orderapp.entity.OrderEntity;
import org.copyria.orderapp.mapper.OrderMapper;
import org.copyria.orderapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Qualifier("serviceAuthCarApi")
    private final CarApi carApi;
    public List<ResponseOrderDto> getOrders(String city, String region, Long minPrice, Long maxPrice) {
        List<OrderEntity> orders;
        if(
                city != null && region != null
                && minPrice != null && maxPrice != null
        ) {
            orders = orderRepository.findAllByCityAndPriceGreaterThanEqualAndPriceLessThanEqual(city, minPrice, maxPrice);
        } else if(
                region != null && minPrice != null && maxPrice != null
        ){
            orders = orderRepository.findAllByRegionAndPriceGreaterThanEqualAndPriceLessThanEqual(region, minPrice, maxPrice);
        } else if (minPrice != null && maxPrice != null) {
            orders = orderRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(minPrice, maxPrice);
        } else if (minPrice != null ) {
             orders = orderRepository.findAllByPriceGreaterThanEqual(minPrice);
        } else if(maxPrice != null) {
            orders = orderRepository.findAllByPriceLessThanEqual(maxPrice);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(order->{
            CarResponseDto car = carApi.getCar(order.getCarId());
            ResponseOrderDto dto = orderMapper.toResponseDto(order);
            dto.setCar(orderMapper.toResposeDto(car));
            return dto;
        }).toList();
    }

    public ResponseOrderDto createOrder(CreateOrderDto dto) {
        var car = createCar(dto);
        OrderEntity order = orderMapper.toEntity(dto);
        order.setCarId(car.getId());
        orderRepository.save(order);
        ResponseOrderDto response =  orderMapper.toResponseDto(order);
        response.setCar(orderMapper.toResposeDto(car));
        return response;
    }
    public CarResponseDto createCar(CreateOrderDto dto){
        return carApi.postCar(orderMapper.toCarCreateDto(dto.getCar()));
    }
    public ResponseOrderDto getOrder(Long id) {
        return orderMapper.toResponseDto(orderRepository.findById(id).get());
    }

    public void deleteOrder(Long id) {
        String carId= orderRepository.findById(id).get().getCarId();
        carApi.deleteCar(carId);
        orderRepository.deleteById(id);
    }

    public Optional<ResponseOrderDto> updateOrder(Long id, UpdateOrderDto dto) {
        return orderRepository.findById(id)
                .map(order->orderMapper.patchOrderEntity(order,dto))
                .map(orderMapper::toResponseDto);
    }
    public void updateOrderStatus(Long id, String status) {
        orderRepository.findById(id).ifPresent(order -> order.setStatus(status));
    }
    public OrderEntity getOrderByCarId(String id) {
        return orderRepository.findByCarId(id);
    }
}
