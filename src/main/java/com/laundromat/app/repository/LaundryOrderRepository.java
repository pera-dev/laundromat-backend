package com.laundromat.app.repository;

import com.laundromat.app.domain.LaundryOrder;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the LaundryOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder, Long> {
}
