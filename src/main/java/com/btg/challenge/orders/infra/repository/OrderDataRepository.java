package com.btg.challenge.orders.infra.repository;

import com.btg.challenge.orders.infra.repository.model.OrderData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDataRepository extends JpaRepository<OrderData, Long> {

    @Query("SELECT o FROM OrderData o WHERE o.customer.customerId = :customerId")
    Page<OrderData> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT o FROM OrderData o LEFT JOIN FETCH o.items WHERE o.orderId = :orderId")
    Optional<OrderData> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(o) FROM OrderData o WHERE o.customer.customerId = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);
}
