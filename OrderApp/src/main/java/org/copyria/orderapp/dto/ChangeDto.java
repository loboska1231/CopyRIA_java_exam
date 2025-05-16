package org.copyria.orderapp.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record ChangeDto(
       String ccy,
       String base_ccy,
       double buy,
       double sale
) {
}
