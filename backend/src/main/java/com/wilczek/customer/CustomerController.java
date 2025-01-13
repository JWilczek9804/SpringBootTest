package com.wilczek.customer;

import com.wilczek.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }

    @RequestMapping( path = "{idCustomer}")
    public Customer getCustomers(@PathVariable(value = "idCustomer") Long idCustomer){
        return customerService.getCustomerById(idCustomer);
    }
    @PostMapping
    public ResponseEntity<?> registerCustomer(
            @RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        ResponseEntity<Object> buildedToken = ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .build();
        return buildedToken;
        // Sposób wysłania tokenu do klienta
    }

    @DeleteMapping(path = "{idCustomer}")
    public void deleteCustomer(@PathVariable(value = "idCustomer") Long id){
        customerService.deleteCustomer(id);
    }

    @PutMapping("{idCustomer}")
    public void updateCustomer(
            @PathVariable(value = "idCustomer") Long id,
            @RequestBody CustomerUpdateRequest request){
        customerService.updateCustomer(id,request);
    }

//    @PostMapping
//    public void registerCustomer(
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "age", required = false) Integer age){
//        customerService.addCustomer(new CustomerRegistrationRequest(name,email,age));
//    }
}
