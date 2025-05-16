package org.copyria.orderapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.copyria.orderapp.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carId;
    private double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private int editTimes;
    private String city;
    private String region;
    private String status;
    private String owner_email;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="orderNViews",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="orderViews_id")
    )
    private List<OrderViews> orderViews;

}
