package com.btg.challenge.orders.infra.dataprovider;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.infra.dataprovider.mapper.CustomerRepositoryMapper;
import com.btg.challenge.orders.infra.repository.CustomerDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerDataProviderImpl implements CustomerDataProvider {

    private final CustomerDataRepository customerRepository;
    private final CustomerRepositoryMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        var customerData = customerMapper.toData(customer);
        var savedCustomerData = customerRepository.save(customerData);
        return customerMapper.toDomain(savedCustomerData);
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByIdWithOrders(Long customerId) {
        return customerRepository.findByIdWithOrders(customerId)
                .map(customerMapper::toDomain);
    }

    @Override
    public long countOrdersByCustomerId(Long customerId) {
        return customerRepository.countOrdersByCustomerId(customerId);
    }
}
