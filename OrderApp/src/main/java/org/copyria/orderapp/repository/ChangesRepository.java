package org.copyria.orderapp.repository;

import org.copyria.orderapp.entity.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangesRepository extends JpaRepository<Change, Long> {
    Change findByCcy(String ccy);
}
