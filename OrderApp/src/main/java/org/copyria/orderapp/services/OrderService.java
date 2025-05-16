package org.copyria.orderapp.services;

import lombok.RequiredArgsConstructor;
import org.copyria.orderapp.client.rest.api.CarApi;
import org.copyria.orderapp.client.rest.model.*;
import org.copyria.orderapp.entity.OrderEntity;
import org.copyria.orderapp.entity.OrderViews;
import org.copyria.orderapp.enums.Currency;
import org.copyria.orderapp.mapper.OrderMapper;
import org.copyria.orderapp.repository.ChangesRepository;
import org.copyria.orderapp.repository.OrderRepository;
import org.copyria.orderapp.repository.OrderViewsRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ChangesRepository changesRepository;
    private final UserService userService;
    private final OrderViewsRepository orderViewsRepository;
    @Qualifier("serviceAuthCarApi")
    private final CarApi carApi;
    public List<ResponseOrderDto> getOrders(String city, String region, Double minPrice, Double maxPrice,  String currency) {
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
        return orders.stream().map(order -> {
            CarResponseDto car = carApi.getCar(order.getCarId());
            ResponseOrderDto dto = orderMapper.toResponseDto(order);
            dto.setCar(orderMapper.toResposeDto(car));

            if (currency != null && !order.getCurrency().getValue().equals(currency)) {
                double fromRate = order.getCurrency().getValue().equals("UAH") ? 1.0 : changesRepository.findByCcy(order.getCurrency().getValue()).getBuy();
                double toRate = currency.equals("UAH") ? 1.0 : changesRepository.findByCcy(currency).getBuy();

                double converted = order.getPrice() * fromRate / toRate;
                dto.setPrice(Math.round(converted * 10.0) / 10.0);
                dto.setCurrency(currency);
            }
            return dto;
        }).toList();
    }

    public ResponseOrderDto createOrder(CreateOrderDto dto) {
        var car = createCar(dto.getCar());
        OrderEntity order = orderMapper.toEntity(dto);
        order.setCarId(car.getId());
        orderRepository.save(order);
        ResponseOrderDto response =  orderMapper.toResponseDto(order);
        response.setCar(orderMapper.toResposeDto(car));
        return response;
    }
    public CarResponseDto createCar(CreateOrderCarDto dto){
        return carApi.postCar(orderMapper.toCarCreateDto(dto));
    }

    public ResponseOrderPremiumDto getOrder(Long id,String currency) {
        ResponseOrderPremiumDto dto = orderMapper.toPremiumFromEntity(orderRepository.findById(id).get());
        List<OrderEntity> orders = toSameCurr(orderRepository.findAll(), currency);
        List<OrderEntity> byCity = toSameCurr(orderRepository.findAllByCity(dto.getCity()), currency);
        List<OrderEntity> byRegion = toSameCurr(orderRepository.findAllByRegion(dto.getRegion()), currency);

        if(userService.getRoles().contains("Premium")){

            dto.setAvgPriceCountry(orderRepository.avgPrice(orders));
            dto.setAvgPriceRegion(orderRepository.avgPrice(byCity));
            dto.setAvgPriceCity(orderRepository.avgPrice(byRegion));
            dto.setViewCount(orderViewsRepository.countOrderViews(id, Instant.now()));
            dto.setViewsToday(orderViewsRepository.countOrderViews(id, Instant.now().minus(1, ChronoUnit.DAYS)));
            dto.setViewsWeek(orderViewsRepository.countOrderViews(id, Instant.now().minus(1, ChronoUnit.WEEKS)));
            dto.setViewsMonth(orderViewsRepository.countOrderViews(id, Instant.now().minus(4, ChronoUnit.WEEKS)));
        }
        return dto;
    }

    public void deleteOrder(Long id) {
        String carId= orderRepository.findById(id).get().getCarId();
        carApi.deleteCar(carId);
        orderRepository.deleteById(id);
    }
    public List<OrderEntity> toSameCurr(List<OrderEntity> orders,String currency) {
        return orders.stream().map(order->{
            if (currency != null && !order.getCurrency().getValue().equals(currency)) {
                double fromRate = order.getCurrency().getValue().equals("UAH") ? 1.0 : changesRepository.findByCcy(order.getCurrency().getValue()).getBuy();
                double toRate = currency.equals("UAH") ? 1.0 : changesRepository.findByCcy(currency).getBuy();

                double converted = order.getPrice() * fromRate / toRate;
                order.setPrice(converted);
                order.setCurrency(Currency.fromValue(currency));
            }
            return order;
        }).toList();
    }
    public Optional<ResponseOrderDto> updateOrder(Long id, UpdateOrderDto dto) {
        return orderRepository.findById(id)
                .map(order->orderMapper.patchOrderEntity(order,dto))
                .map(orderMapper::toResponseDto);
    }
    public void updateOrderStatus(Long id, String status) {
        orderRepository.findById(id).ifPresent(order -> {
            if(order.getEditTimes()!= 3) {
                order.setStatus(status);
            }else {
                order.setStatus("NonActive");
                // mailsender
            }
            order.setEditTimes(order.getEditTimes() + 1);

        });
    }
    public void updateViewCount(Long id, int viewCount) {
        OrderViews view = OrderViews.builder().id(id).build();
        OrderEntity order = orderRepository.findById(id).get();

        orderRepository.save(order);
    }
    public OrderEntity getOrderByCarId(String id) {
        return orderRepository.findByCarId(id);
    }
}
