package com.wilczek.customer;

import com.wilczek.exceptions.DuplicateResourceException;
import com.wilczek.exceptions.RequestValidationException;
import com.wilczek.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // Rozszerzona adnotacja @Component
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("JDBC") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }
    public List<Customer> getAllCustomers(){
        return customerDAO.selectAllCustomers();
    }
    public Customer getCustomerById(Long id){
        return customerDAO.selectCustomerById(id).
                orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] has not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        String name = customerRegistrationRequest.name();
        Integer age = customerRegistrationRequest.age();
        Gender gender = customerRegistrationRequest.gender();
        if (customerDAO.existPersonWithEmail(email)) {
            throw new DuplicateResourceException("Email [%s] is already taken".formatted(email));
        }
            Customer customer = new Customer(name, email, "password", age, gender);
            customerDAO.insertCustomer(customer);
    }
    public void deleteCustomer(Long id){
        if (!customerDAO.existPersonWithId(id)){
            throw new ResourceNotFoundException("Customer with id %s not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Long customerId,CustomerUpdateRequest updateRequest){
        // TODO: for JPA use .getReferenceById(customerId) as it does does not bring object into memory and instead a reference
        Customer customer = customerDAO.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDAO.existPersonWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }
        if (updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())) {
            customer.setGender(updateRequest.gender());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDAO.updateCustomer(customer);
    }
}
