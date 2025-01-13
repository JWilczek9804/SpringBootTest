package com.wilczek.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// fake database
@Repository("List")
public class CustomerListDataAccessService implements CustomerDAO {

    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(1L,"Alex","alex@gmail.com", "password", 21, Gender.MALE);
        Customer jamila = new Customer(2L,"Jamila","jamila@gmail.com", "password", 19, Gender.MALE);
        Customer jacob = new Customer(3L,"Jacob","jacob@gmail.com", "password", 22, Gender.MALE);
        customers.add(alex);
        customers.add(jamila);
        customers.add(jacob);
    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public void deleteCustomerById(Long id) {
        customers.remove(id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.stream()
                .filter(c -> c.getId().equals(customer.getId()))
                .map(c1 -> c1 = customer);
    };


    @Override
    public boolean existPersonWithEmail(String email) {
//        boolean check = false;
//        for (int i = 0; i < customers.size(); i++) {
//            if (customers.get(i).getEmail().equals(email)){
//                check = true;
//                break;
//            }else check = false;
//        }
//        return check;
        return customers.stream().anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existPersonWithId(Long id) {
        return customers.stream().anyMatch(c -> c.getId().equals(id));
    }
}
