package com.wilczek.customer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CustomerRowMapperTest {
    @Test
    void mapRow() throws SQLException {
        // Given
        int rowNumber = 1;
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);
        Mockito.when(rs.getLong("id")).thenReturn(1L);
        Mockito.when(rs.getString("name")).thenReturn("jakub");
        Mockito.when(rs.getString("email")).thenReturn("jakub@cpk.pl");
        Mockito.when(rs.getString("password")).thenReturn("password");
        Mockito.when(rs.getInt("age")).thenReturn(33);
        Mockito.when(rs.getString("gender")).thenReturn("FEMALE");
        // When
        Customer customerRM = customerRowMapper.mapRow(rs, rowNumber);
        System.out.println(customerRM);
        // Then
        Customer customer = new Customer(1L,"jakub","jakub@cpk.pl", "password", 33, Gender.FEMALE);
        assertThat(customerRM.getId()).isEqualTo(customer.getId());
        assertThat(customerRM.getName()).isEqualTo(customer.getName());
        assertThat(customerRM.getEmail()).isEqualTo(customer.getEmail());
        assertThat(customerRM.getPassword()).isEqualTo(customer.getPassword());
        assertThat(customerRM.getAge()).isEqualTo(customer.getAge());
        assertThat(customerRM.getGender()).isEqualTo(customer.getGender());
    }
}