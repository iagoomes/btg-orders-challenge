package com.btg.challenge.orders.infra.repository;

import com.btg.challenge.orders.infra.repository.model.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDataRepository extends JpaRepository<CustomerData, Long> {

    @Query("SELECT COUNT(o) FROM OrderData o WHERE o.customer.customerId = :customerId")
    long countOrdersByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM CustomerData c LEFT JOIN FETCH c.orders WHERE c.customerId = :customerId")
    Optional<CustomerData> findByIdWithOrders(@Param("customerId") Long customerId);
}
