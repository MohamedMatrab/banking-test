package com.exam_jee.ds.repositories;

import com.exam_jee.ds.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByClientIdOrBeneficiaireId(Long clientId,Long beneficiaireId);
}
