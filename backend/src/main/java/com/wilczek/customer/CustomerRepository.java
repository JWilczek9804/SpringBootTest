package com.wilczek.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Transactional
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Long id);

    boolean existsCustomerByEmail(String email);
}
 