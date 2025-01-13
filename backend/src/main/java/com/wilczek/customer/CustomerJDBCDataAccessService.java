package com.wilczek.customer;

import com.wilczek.exceptions.RequestValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("JDBC")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }
    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id,name,email,password,age,gender
                FROM customer
                """;
//        RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
//            Customer customer = new Customer(
//                    rs.getLong("id"),
//                    rs.getString("name"),
//                    rs.getString("email"),
//                    rs.getInt("age")
//            );
//            return customer;
//        };
//        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);
        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        String sql = """
                SELECT id,name,email,password,age,gender
                FROM customer
                WHERE id=?
                """;
//        var query = jdbcTemplate.query(sql,id);

        return jdbcTemplate.query(sql, customerRowMapper,id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name,email,password,age,gender)
                VALUES(?,?,?,?,?)
                """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAge(),
                customer.getGender().name()
        );
        System.out.println("JDBC template: " + result);
    }

    @Override
    public void deleteCustomerById(Long id) {
        var sql = "delete from customer where id = ?";
        jdbcTemplate.update(sql,id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        boolean change = false;

        if (customer.getName() != null){
            change = true;
            String sql = "UPDATE customer SET name=? WHERE id=?";
            jdbcTemplate.update(sql,customer.getName(),customer.getId());
        }
        if (customer.getEmail() != null){
            change = true;
            String sql = "UPDATE customer SET email=? WHERE id=?";
            jdbcTemplate.update(sql,customer.getEmail(),customer.getId());
        }
        if (customer.getAge() != null){
            change = true;
            String sql = "UPDATE customer SET age=? WHERE id=?";
            jdbcTemplate.update(sql,customer.getAge(),customer.getId());
        }
        if (customer.getGender() != null){
            change = true;
            String sql = "UPDATE customer SET gender=? WHERE id=?";
            jdbcTemplate.update(sql,customer.getGender().name(),customer.getId());
        }
        if (customer.getPassword() != null){
            change = true;
            String sql = "UPDATE customer SET password=? WHERE id=?";
            jdbcTemplate.update(sql,customer.getPassword(),customer.getId());
        }

        if (!change){
            throw new RequestValidationException("Any changes not found");
        }
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email=?
                """;
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,email);
        return count != null && count >0;
    }

    @Override
    public boolean existPersonWithId(Long id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id=?
                """;
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,id);
        return count != null && count >0;
    }
}
