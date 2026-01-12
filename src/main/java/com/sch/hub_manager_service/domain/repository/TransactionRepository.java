package com.sch.hub_manager_service.domain.repository;

import com.sch.hub_manager_service.domain.model.persistency.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
