package com.wilczek.journey;

import com.github.javafaker.Faker;
import com.wilczek.customer.Customer;
import com.wilczek.customer.CustomerRegistrationRequest;
import com.wilczek.customer.CustomerUpdateRequest;
import com.wilczek.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {
    // In order to install Web Test Client we need to install
    // the Flux package for SpringBoot
    @Autowired
    private WebTestClient webTestClient;

    private final static String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void registerCustomer() {

        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.random().nextInt(20, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        // get a post request
        webTestClient
                .post()// Inicjujemy żądanie POST
                .uri(CUSTOMER_URI)// wskazujemy na endpoint, do którego żądanie zostanie wysłane.
                .accept(MediaType.APPLICATION_JSON) // oznacza, że oczekujemy odpowiedzi w formacie JSON.
                .contentType(MediaType.APPLICATION_JSON) // oznacza, że przesyłamy dane w formacie JSON
                .body(Mono.just(request), CustomerRegistrationRequest.class)// tworzy Mono, które emituje pojedynczą wartość request.
                .exchange()// Wykonujemy wymianę HTTP
                .expectStatus() // Sprawdzamy, czy status odpowiedzi jest OK (200).
                .isOk();
        // get all customers
        List<Customer> responseBody = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        long id = responseBody
                .stream()
                .filter(e -> e.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        System.out.println(id);

        Customer expectedCustomer = new Customer(id,name, email, "password", age, Gender.MALE);
        // make sure that customer is present
        assertThat(responseBody)
                .contains(expectedCustomer);
        // get customer by id
        webTestClient
                .get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void deleteCustomerTest() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.random().nextInt(20, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        // get a post request
        webTestClient
                .post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        Customer expectedCustomer = new Customer(name, email, "password", age, Gender.MALE);
        long id = allCustomers
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);
        // delete customer
        webTestClient
                .delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        // verify of existing deleted customer
        webTestClient
                .get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void updateCustomerTest() {

        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().safeEmailAddress();
        int age = faker.random().nextInt(20, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

                // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        // get a post request
        webTestClient
                .post()// Inicjujemy żądanie POST
                .uri(CUSTOMER_URI)// wskazujemy na endpoint, do którego żądanie zostanie wysłane.
                .accept(MediaType.APPLICATION_JSON) // oznacza, że oczekujemy odpowiedzi w formacie JSON.
                .contentType(MediaType.APPLICATION_JSON) // oznacza, że przesyłamy dane w formacie JSON
                .body(Mono.just(request), CustomerRegistrationRequest.class)// tworzy Mono, które emituje pojedynczą wartość request.
                .exchange()// Wykonujemy wymianę HTTP
                .expectStatus() // Sprawdzamy, czy status odpowiedzi jest OK (200).
                .isOk();
        // get all customers
        List<Customer> responseBody = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        Customer expectedCustomer = new Customer(name, email, "password", age, gender);
        // make sure that customer is present
        assertThat(responseBody)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);
        System.out.println(expectedCustomer);
        // get customer by id
        long id = responseBody
                .stream()
                .filter(e -> e.getEmail().equals(expectedCustomer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        System.out.println(id);
        CustomerUpdateRequest updateRequest =
                new CustomerUpdateRequest("Test", email, age, gender);
        // update Customer
        webTestClient
                .put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // data of updatedCustomer
        Customer updatedCustomer = webTestClient
                .get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        System.out.println(updatedCustomer);
        // verify data of updated customer
        assertThat(updatedCustomer.getId()).isEqualTo(id);
        assertThat(updatedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(updatedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }
}
