package org.copyria.orderapp.mapper;


import org.copyria.orderapp.client.rest.model.*;
import org.copyria.orderapp.dto.ChangeDto;
import org.copyria.orderapp.entity.Change;
import org.copyria.orderapp.entity.OrderEntity;
import org.copyria.orderapp.enums.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMapper {
    @Mapping(target = "status", constant = "CREATE")
    @Mapping(target ="owner_email",source = "ownerEmail")
    OrderEntity toEntity(CreateOrderDto order);
    default ResponseOrderPremiumDto toPremium(ResponseOrderDto order){
        return new  ResponseOrderPremiumDto()
                .city(order.getCity())
                .region(order.getRegion())
                .price(order.getPrice())
                .status(order.getStatus())
                .car(order.getCar())
                .ownerEmail(order.getOwnerEmail())
                .currency(order.getCurrency());
    }
    default ResponseOrderPremiumDto toPremiumFromEntity(OrderEntity order){
        return toPremium(toResponseDto(order));
    }
    @Mapping(target ="owner_email",source = "ownerEmail")
    @Mapping(target = "status", constant = "UPDATED")
    @Mapping(ignore = true,target = "orderViews")
    @Mapping(ignore = true,target = "carId")
    @Mapping(ignore = true,target = "id")
    OrderEntity patchOrderEntity(@MappingTarget OrderEntity existing, UpdateOrderDto dto);
    Change toChangeEntity(ChangeDto dto);
    @Mapping(target ="ownerEmail",source = "owner_email")
    ResponseOrderDto toResponseDto(OrderEntity order);
    CreateCarDto toCarCreateDto(CreateOrderCarDto dto);
    ResponseOrderCarDto toResposeDto(CarResponseDto car);
}
