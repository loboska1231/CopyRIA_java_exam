package org.copyria.orderapp.repository;

import org.copyria.orderapp.entity.OrderViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface OrderViewsRepository extends JpaRepository<OrderViews, Long> {
    @Query("select count(v) from OrderViews v where v.orderId = :orderId and v.orderDate >=:from ")
    int countOrderViews(Long orderId, Instant from);
}
