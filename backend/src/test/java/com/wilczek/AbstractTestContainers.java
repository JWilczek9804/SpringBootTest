package com.wilczek;

import com.github.javafaker.Faker;
import com.wilczek.customer.Customer;
import com.wilczek.customer.Gender;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Random;

@Testcontainers
public abstract class AbstractTestContainers {

    @BeforeAll
    static void canApplyMigrationsWithFlyway() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("amigoscode-dao-unit-test")
                    .withUsername("amigoscode")
                    .withPassword("password");

    /*
    Poniższa adnitacja jest używana do dynamicznego dodawania właściwości do kontekstu aplikacji
    podczas uruchamiania testów. Pozwala to na dostosowanie właściwości konfiguracyjnych
    (np. połączenia do bazy danych) w czasie wykonywania testów, co jest szczególnie
    przydatne przy używaniu dynamicznych zasobów, takich jak kontenery Docker.
    */
    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry){
//        registry.add(
//                "spring.datasource.url",
//                () -> String.format(
//                        "jdbc:postgresql://localshost:%d/customer",
//                        postgreSQLContainer.getFirstMappedPort()
//                )
//        );
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    private static DataSource dataSource(){
        DataSourceBuilder builder = DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());
        return builder.build();
    }
    protected static JdbcTemplate getJDBCTemplate(){
        return new JdbcTemplate(dataSource());
    }


    protected final static Faker FAKER = new Faker();

    protected String createEmail(){
        return FAKER.internet().safeEmailAddress();
    }
    protected Customer createCustomer(){
        return new Customer(
                FAKER.name().fullName(),
                createEmail(),
                "password",
                new Random().nextInt(30,99),
                Gender.MALE);
    }


}
