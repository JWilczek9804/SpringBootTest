package com.wilczek.customer;

import java.util.List;
import java.util.Optional;


public interface CustomerDAO {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long id);

    void insertCustomer(Customer customer);

    void deleteCustomerById(Long id);

    void updateCustomer(Customer customer);

    boolean existPersonWithEmail(String email);

    boolean existPersonWithId(Long id);
}
