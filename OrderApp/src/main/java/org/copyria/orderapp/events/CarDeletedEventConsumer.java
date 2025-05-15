package org.copyria.orderapp.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.copyria.orderapp.api.event.consumer.IOnCarDeletedEventConsumerService;
import org.copyria.orderapp.api.event.model.CarDeletedEventPayload;
import org.copyria.orderapp.services.OrderService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarDeletedEventConsumer implements IOnCarDeletedEventConsumerService {
    private final OrderService orderService;

    @Override
    public void onCarDeletedEvent(CarDeletedEventPayload payload, CarDeletedEventPayloadHeaders headers) {
            log.info("Received event: {}",payload);

        orderService.updateOrderStatus(orderService.getOrderByCarId(payload.getCarId()).getId(),"CANCELLED");
    }
}
