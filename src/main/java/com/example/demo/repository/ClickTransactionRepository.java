package com.example.demo.repository;

import com.example.demo.model.ClickTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickTransactionRepository extends JpaRepository<ClickTransaction, Long> {
    List<ClickTransaction> findAllByOrderByIdDesc();
    List<ClickTransaction> findByClickTransId(Long clickTransId);
    List<ClickTransaction> findByOrderId(Long orderId);
}
