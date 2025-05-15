package org.copyria.orderapp.mapper;


import org.copyria.orderapp.client.rest.model.*;
import org.copyria.orderapp.entity.OrderEntity;
import org.copyria.orderapp.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMapper {
    default OrderEntity toEntity(CreateOrderDto order){
        Currency curr = Currency.fromValue(order.getCurrency().getValue());
        return OrderEntity.builder()
                .price(order.getPrice())
                .city(order.getCity())
                .region(order.getRegion())
                .owner_email(order.getOwnerEmail())
                .status("ACTIVE")
                .currency(curr)
                .build();
    };
    default OrderEntity patchOrderEntity(OrderEntity existing, UpdateOrderDto dto){
        Currency curr = dto.getCurrency() ==null ? existing.getCurrency(): Currency.fromValue(dto.getCurrency().getValue());
        return OrderEntity.builder()
                .city(dto.getCity() == null ? existing.getCity() : dto.getCity())
                .price(dto.getPrice() == null ? existing.getPrice() : dto.getPrice())
                .region(dto.getRegion() == null ?  existing.getRegion() : dto.getRegion())
                .owner_email(dto.getOwnerEmail() == null ? existing.getOwner_email() : dto.getOwnerEmail())
                .status(existing.getStatus())
                .currency(curr)
                .build();
    };
    ResponseOrderDto toResponseDto(OrderEntity order);
    CreateCarDto toCarCreateDto(CreateOrderCarDto dto);
    ResponseOrderCarDto toResposeDto(CarResponseDto car);
}
