package org.copyria.orderapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.copyria.orderapp.enums.Currency;

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
    private Long price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String city;
    private String region;
    private String status;
    private String owner_email;
}
