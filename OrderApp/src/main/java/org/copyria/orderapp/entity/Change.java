package org.copyria.orderapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "changeCourses")
public class Change {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ccy;
    private String base_ccy;
    private double buy;
    private double sale;


}
