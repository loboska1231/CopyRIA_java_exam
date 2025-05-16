package org.copyria.orderapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orderViews")
public class OrderViews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Instant orderDate;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(
            name="orderNViews",
          joinColumns = @JoinColumn(name="orderViews_id"),
          inverseJoinColumns = @JoinColumn(name="order_id")
    )
    private OrderEntity order;
}
