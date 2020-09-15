package com.laundromat.app.repository;

import com.laundromat.app.domain.UserAccount;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the UserAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
