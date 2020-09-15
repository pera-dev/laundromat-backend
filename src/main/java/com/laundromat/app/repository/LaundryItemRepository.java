package com.laundromat.app.repository;

import com.laundromat.app.domain.LaundryItem;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the LaundryItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LaundryItemRepository extends JpaRepository<LaundryItem, Long> {
}
