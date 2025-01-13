package com.wilczek.customer;

import com.wilczek.AbstractTestContainers;
import com.wilczek.exceptions.DuplicateResourceException;
import com.wilczek.exceptions.RequestValidationException;
import com.wilczek.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest extends AbstractTestContainers {

    @Mock
    private CustomerDAO customerDAO;
    @InjectMocks
    private CustomerService underTest;
    /*
    @InjectMocks powoduje automatyczne wstrzyknięcie mocków do wskazanej klasy.
    Natomiast szczegóły działania tego mechanizmu przekraczają zakres tego wpisu.
    */

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();
        // Then
        Mockito.verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // Given
        Long id = 3L;
        Customer c1 = createCustomer();
        c1.setId(id);

        Mockito.when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.of(c1));
        // When
        Customer actual = underTest.getCustomerById(id);
        // Then
        assertThat(actual).isEqualTo(c1);
    }

    @Test
    void whenGetCustomerByIdDoesNotFound() {
        // Given
        Long id = 10L;
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] has not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        Mockito.when(customerDAO.existPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Jamal",
                email,
                33,
                Gender.MALE
        );
        // When
        underTest.addCustomer(request);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(request.gender());
    }

    @Test
    void whenAddingCustomerIsExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Jamal",
                email,
                33,
                Gender.MALE
        );
        Mockito.when(customerDAO.existPersonWithEmail(email)).thenReturn(true);
        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email [%s] is already taken".formatted(email));
        // Then
        Mockito.verify(customerDAO,Mockito.never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomer() {
        // Given
        long id = 1L;
        Mockito.when(customerDAO.existPersonWithId(id)).thenReturn(true);
        // When
        underTest.deleteCustomer(id);
        // Then
        Mockito.verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void deletingCustomerWhenNotExist() {
        // Given
        long id = 1L;
        Mockito.when(customerDAO.existPersonWithId(id)).thenReturn(false);
        // When
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id %s not found".formatted(id));
        // Then
        Mockito.verify(customerDAO,Mockito.never()).deleteCustomerById(Mockito.any());
    }

    @Test
    void updateAllCustomerProperties() {
        // Given
        Long id = 10L;
        Customer customer = createCustomer();
        customer.setId(id);
        System.out.println(customer);
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "James",
                "JamesOrton@cpk.pl",
                40,
                Gender.MALE
        );
        // When
        Mockito.when(customerDAO.existPersonWithEmail(updateRequest.email())).thenReturn(false);
        underTest.updateCustomer(id,updateRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        System.out.println(capturedCustomer);
        // Then
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(updateRequest.gender());

    }

    @Test
    void updateCustomerName() {
        // Given
        Long id = 1L;
        String updatedName = FAKER.name().fullName();
        Customer customer = createCustomer();
        customer.setId(id);
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest update = new CustomerUpdateRequest(
                updatedName,
                customer.getEmail(),
                customer.getAge(),
                customer.getGender()
        );

        // When
        underTest.updateCustomer(id,update);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getName()).isEqualTo(update.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());

    }

    @Test
    void updateCustomerEmail() {
        // Given
        Long id = 1L;
        String updatedEmail = FAKER.internet().safeEmailAddress();
        Customer customer = createCustomer();
        customer.setId(id);
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest update = new CustomerUpdateRequest(
                customer.getName(),
                updatedEmail,
                customer.getAge(),
                customer.getGender()
        );

        // When
        underTest.updateCustomer(id,update);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(update.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomerAge() {
        // Given
        Long id = 1L;
        Integer updatedAge = FAKER.random().nextInt(20,100);
        Customer customer = createCustomer();
        customer.setId(id);
        System.out.println(customer);
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest update = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                updatedAge,
                customer.getGender()
        );

        // When
        underTest.updateCustomer(id,update);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        System.out.println(capturedCustomer);

        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(update.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomerGender() {
        // Given
        Long id = 1L;
        Gender updatedGender = Gender.FEMALE;
        Customer customer = createCustomer();
        customer.setId(id);
        System.out.println(customer);
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest update = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                updatedGender
        );

        // When
        underTest.updateCustomer(id,update);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        System.out.println(capturedCustomer);

        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(update.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updatingCustomerOfNotFoundId() {
        // Given
        long id = 1L;
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        CustomerUpdateRequest update = new CustomerUpdateRequest(
                "Jonas",
                "Jonas@cpk.pl",
                33,
                Gender.MALE
        );
        // When
        assertThatThrownBy(()-> underTest.updateCustomer(id,update))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
        // Then
        Mockito.verify(customerDAO,Mockito.never()).updateCustomer(Mockito.any());
    }


    @Test
    void throwUpdatedCustomerWhenEmailIsTaken() {
        // Given
        long id = 1L;
        Customer customer = createCustomer();
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String email = FAKER.internet().safeEmailAddress();
        Mockito.when(customerDAO.existPersonWithEmail(email)).thenReturn(true);
        CustomerUpdateRequest update = new CustomerUpdateRequest(
                customer.getName(),
                email,
                customer.getAge(),
                customer.getGender()
        );
        // When
        assertThatThrownBy(()-> underTest.updateCustomer(id,update))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
        // Then
        Mockito.verify(customerDAO,Mockito.never()).updateCustomer(Mockito.any());
    }

    @Test
    void whenIsNothingToUpdate() {
        // Given
        Long id = 1L;
        Customer customer = createCustomer();
        CustomerUpdateRequest update = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender()
        );
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id,update))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");
        // Then
        Mockito.verify(customerDAO,Mockito.never()).updateCustomer(Mockito.any());
    }
}