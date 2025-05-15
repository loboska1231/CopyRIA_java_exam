package org.copyria.orderapp.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.copyria.orderapp.client.rest.controller.OrdersApi;
import org.copyria.orderapp.client.rest.model.CreateOrderDto;
import org.copyria.orderapp.client.rest.model.ResponseOrderDto;
import org.copyria.orderapp.client.rest.model.UpdateOrderDto;
import org.copyria.orderapp.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {
    private final OrderService orderService;
    @RolesAllowed("MANAGER")
    @Override
    public ResponseEntity<Void> deleteOrder(Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    @RolesAllowed("BUYER")
    @Override
    public ResponseEntity<ResponseOrderDto> getOrder(Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @Override
    public ResponseEntity<List<ResponseOrderDto>> getOrders(String city, String region, Long minPrice, Long maxPrice) {
        return ResponseEntity.ok(orderService.getOrders(city, region, minPrice, maxPrice));
    }
    @RolesAllowed("SELLER")
    @Override
    public ResponseEntity<ResponseOrderDto> postOrder(CreateOrderDto createOrderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(createOrderDto));
    }
    @RolesAllowed("SELLER")
    @Override
    public ResponseEntity<ResponseOrderDto> updateOrder(Long id, UpdateOrderDto updateOrderDto) {
        return ResponseEntity.of(orderService.updateOrder(id,updateOrderDto));
    }
}
