package com.wilczek.customer;

import com.wilczek.AbstractTestContainers;
import com.wilczek.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();


    public long getId(Customer customer){
        return underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
    }

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJDBCTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = createCustomer();

        underTest.insertCustomer(customer);

        // When
        List<Customer> customers = underTest.selectAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
        System.out.println();
    }

    @Test
    void selectCustomerById() {
        // Given
        Customer customer = createCustomer();

        underTest.insertCustomer(customer);
        long id = getId(customer);
        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v -> {
            assertThat(v.getId()).isEqualTo(id);
            assertThat(v.getName()).isEqualTo(customer.getName());
            assertThat(v.getEmail()).isEqualTo(customer.getEmail());
            assertThat(v.getAge()).isEqualTo(customer.getAge());
            }
        );
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        long id = -1;

        // When
        Optional<Customer> customer = underTest.selectCustomerById(id);

        // Then
        assertThat(customer).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given
        Customer customer = createCustomer();
        // When
        underTest.insertCustomer(customer);
        // Then
        List<Customer> actual = underTest.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void deleteCustomerById() {

        // Given
        Customer customer = createCustomer();

        underTest.insertCustomer(customer);
        long id = getId(customer);
        // When
        underTest.deleteCustomerById(id);

        Optional<Customer> customer1 = underTest.selectCustomerById(id);

        // Then
        assertThat(customer1).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        String newName = FAKER.name().fullName();
        long id = getId(customer);

        // When
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v ->{
           assertThat(v.getId()).isEqualTo(id);
           assertThat(v.getName()).isEqualTo(newName); //change
           assertThat(v.getEmail()).isEqualTo(customer.getEmail());
           assertThat(v.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerEmail() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        String newEmail = FAKER.internet().safeEmailAddress();
        Long id = getId(customer);
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);
        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v ->{
           assertThat(v.getId()).isEqualTo(id);
           assertThat(v.getEmail()).isEqualTo(newEmail); // change
           assertThat(v.getPassword()).isEqualTo(customer.getPassword());
           assertThat(v.getName()).isEqualTo(customer.getName());
           assertThat(v.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerPassword() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        String newPassword = FAKER.internet().password();
        Long id = getId(customer);
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setPassword(newPassword);
        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v ->{
            assertThat(v.getId()).isEqualTo(id);
            assertThat(v.getEmail()).isEqualTo(customer.getEmail()); // change
            assertThat(v.getPassword()).isEqualTo(newPassword);
            assertThat(v.getName()).isEqualTo(customer.getName());
            assertThat(v.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given

        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        int newSetAge = new Random().nextInt(30,99);
        long id = getId(customer);
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newSetAge);
        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v ->{
            assertThat(v.getId()).isEqualTo(id);
            assertThat(v.getAge()).isEqualTo(newSetAge); // change
            assertThat(v.getName()).isEqualTo(customer.getName());
            assertThat(v.getEmail()).isEqualTo(customer.getEmail());
        });

    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);


        Long id = getId(customer);
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        String email = UUID.randomUUID().toString();
        update.setEmail(email);
        update.setAge(22);

        underTest.updateCustomer(update);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(id);
            assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            assertThat(updated.getName()).isEqualTo("foo");
            assertThat(updated.getEmail()).isEqualTo(email);
            assertThat(updated.getAge()).isEqualTo(22);
        });

        System.out.println();
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        Long id = getId(customer);
        // When

        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(v ->{
            assertThat(v.getId()).isEqualTo(id);
            assertThat(v.getName()).isEqualTo(customer.getName());
            assertThat(v.getEmail()).isEqualTo(customer.getEmail());
            assertThat(v.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existPersonWithEmail() {
        // Given
        Customer customer = createCustomer();
        String email = customer.getEmail();
        underTest.insertCustomer(customer);
        // When
        boolean check = underTest.existPersonWithEmail(email);
        // Then
        assertThat(check).isTrue();
    }

    @Test
    void existPersonWithId() {
        // Given
        Customer customer = createCustomer();
        underTest.insertCustomer(customer);

        long id = getId(customer);
        // When
        boolean check = underTest.existPersonWithId(id);

        // Then
        assertThat(check).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        // When
        boolean b = underTest.existPersonWithEmail(email);
        // Then
        assertThat(b).isFalse();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenDoesNotExists() {
        // Given
        long id = -1;
        // When
        boolean check = underTest.existPersonWithId(id);
        // Then
        assertThat(check).isFalse();
    }
}