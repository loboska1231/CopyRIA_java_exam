package org.copyria.orderapp.services;

import org.copyria.orderapp.client.rest.api.CarApi;
import org.copyria.orderapp.client.rest.model.*;
import org.copyria.orderapp.entity.Change;
import org.copyria.orderapp.entity.OrderEntity;
import org.copyria.orderapp.enums.Currency;
import org.copyria.orderapp.mapper.OrderMapper;
import org.copyria.orderapp.mapper.OrderMapperImpl;
import org.copyria.orderapp.repository.ChangesRepository;
import org.copyria.orderapp.repository.OrderRepository;
import org.copyria.orderapp.repository.OrderViewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orderRepository;
    @Mock ChangesRepository changesRepository;
    @Mock UserService userService;
    @Mock OrderViewsRepository orderViewsRepository;
    @Mock CarApi carApi;
    @Spy OrderMapper mapper = new OrderMapperImpl();
    @InjectMocks OrderService service;

    private OrderEntity sampleOrder;
    private CarResponseDto sampleCar;

    @BeforeEach
    void setup() {
        sampleOrder = OrderEntity.builder()
                .id(1L)
                .carId("car-123")
                .price(100.0)
                .currency(Currency.UAH)
                .city("Kyiv")
                .region("Kyiv")
                .status("ACTIVE")
                .owner_email("user@example.com")
                .build();
        sampleCar = new CarResponseDto().id("car-123").model("Model").producer("Make").year(BigDecimal.valueOf(2020));
    }
    @Test
    void getOrders_noFilters_returnsAll() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(carApi.getCar(anyString())).thenReturn(sampleCar);
        var result = service.getOrders(null, null, null, null, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCar().getYear()).isEqualTo(2020);
        verify(orderRepository).findAll();
    }
    @Test
    void getOrders_withCurrencyConversion_convertsPrice() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(carApi.getCar(anyString())).thenReturn(sampleCar);
        when(changesRepository.findByCcy("USD")).thenReturn(new Change(1L,"USD","UAH",40.0,50.0));
        var result = service.getOrders(null, null, null, null, "USD");
        assertThat(result.get(0).getPrice()).isEqualTo(Math.round(100.0 * 1.0 / 40.0 * 10.0) / 10.0);
        assertThat(result.get(0).getCurrency()).isEqualTo("USD");
    }
    static Stream<Arguments> cityRegion(){
        return Stream.of(
                Arguments.arguments("kiev","dnepropetrodsk"),
                Arguments.arguments(null,"Dnipro"),
                Arguments.arguments(null,null),
                Arguments.arguments("lviv","lviv")
        );
    }

    @ParameterizedTest
    @MethodSource("cityRegion")
    void getOrders_ShouldAcceptAnyPriceValues(String city, String region) {
        Assertions.assertDoesNotThrow(() -> service.getOrders(city, region, null, null, null));
    }
    static Stream<Arguments> minMax(){
        return Stream.of(
                Arguments.arguments(-1.0,20.0),
                Arguments.arguments(20.0,140.0),
                Arguments.arguments(101.0,140.0),
                Arguments.arguments(0.0,0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("minMax")
    void getOrders_ShouldAcceptAnyPriceValues(double minPrice, double maxPrice) {
        Assertions.assertDoesNotThrow(() -> service.getOrders(null, null, minPrice, maxPrice, null));
    }
    @Test
    void createCar_delegatesToApi() {
        CreateOrderCarDto carDto = new CreateOrderCarDto()
                .model("ModelX").producer("MakeY").year(2021.0);
        when(carApi.postCar(any())).thenReturn(sampleCar);
        CarResponseDto result = service.createCar(carDto);
        assertThat(result).isSameAs(sampleCar);
        verify(carApi).postCar(mapper.toCarCreateDto(carDto));
    }
    @Test
    void createOrder_savesEntityAndReturnsDto() {
        CreateOrderDto dto = new CreateOrderDto()
                .price(200.0)
                .currency(CreateOrderDto.CurrencyEnum.UAH)
                .city("Kyiv")
                .region("Kyiv")
                .ownerEmail("owner@example.com")
                .car(new CreateOrderCarDto().model("M").producer("P").year(2022.0));

        when(carApi.postCar(any())).thenReturn(sampleCar);
        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        when(orderRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        ResponseOrderDto resp = service.createOrder(dto);

        OrderEntity saved = captor.getValue();
        assertThat(saved.getCarId()).isEqualTo("car-123");
        assertThat(resp.getCar().getModel()).isEqualTo("Model");
        verify(orderRepository).save(any(OrderEntity.class));
    }


    @Test
    void getOrder_nonPremiumUser_skipsStats() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(userService.getRoles()).thenReturn(Set.of("Buyer"));
        ResponseOrderPremiumDto prem = service.getOrder(1L, "UAH");
        assertThat(prem.getAvgPriceCountry()).isNull();
        assertThat(prem.getViewCount()).isNull();
        verify(orderViewsRepository, never()).countOrderViews(anyLong(), any());
    }
    @Test
    void deleteOrder_shouldCallCarApiAndRepository() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        service.deleteOrder(10L);
        verify(carApi).deleteCar("car-123");
        verify(orderRepository).deleteById(10L);
    }

    @Test
    void toSameCurr_withDifferentCurrency_convertsList() {
        sampleOrder.setCurrency(Currency.USD);
        sampleOrder.setPrice(100.0);
        when(changesRepository.findByCcy("USD")).thenReturn(
                new Change(1L,"USD", "UAH", 30.0, 0));
        List<OrderEntity> converted = service.toSameCurr(List.of(sampleOrder), "UAH");
        assertThat(converted).hasSize(1);
        OrderEntity o = converted.get(0);
        assertEquals(100.0 * 30.0, o.getPrice());
        assertEquals(Currency.UAH, o.getCurrency());
    }

    @Test
    void updateOrder_withExistingOrder_returnsPatchedDto() {
        UpdateOrderDto dto = new UpdateOrderDto().region("Plak");
        sampleOrder.setRegion("Plak");
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        Optional<ResponseOrderDto> result = service.updateOrder(10L, dto);
        assertThat(result).isPresent();
        assertEquals("UPDATED", result.get().getStatus());
    }

    @Test
    void updateOrderStatus_lessThanMaxIncrements() {
        sampleOrder.setEditTimes(1);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        service.updateOrderStatus(10L, "NEW_STATUS");
        assertEquals("NEW_STATUS", sampleOrder.getStatus());
        assertEquals(2, sampleOrder.getEditTimes());
    }

    @Test
    void updateOrderStatus_maxTimes_setsNonActive() {
        sampleOrder.setEditTimes(3);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        service.updateOrderStatus(10L, "ANY");
        assertEquals("NonActive", sampleOrder.getStatus());
        assertEquals(4, sampleOrder.getEditTimes());
    }

    @Test
    void updateViewCount_savesOrder() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        service.updateViewCount(10L, 5);
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void getOrderByCarId_findsEntity() {
        when(orderRepository.findByCarId("car-10")).thenReturn(sampleOrder);
        OrderEntity result = service.getOrderByCarId("car-10");
        assertEquals(sampleOrder, result);
    }
}