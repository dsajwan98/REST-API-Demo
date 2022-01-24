package com.example.test.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.demo.model.Customer;
import com.example.test.demo.model.ResponseObject;
import com.example.test.demo.service.CustomerService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class CustomerController {
    @Autowired
    private CustomerService service;

	@PostMapping(value = {"/customers/register", "/api/customers/register"})
    public ResponseEntity<ResponseObject> registerUser(@RequestBody Customer Customer) {
        String success = "Registration successful";
        ResponseObject response = service.saveCustomer(Customer);
        if (response.getMessage().equals(success)) {
            return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ResponseObject>(response, HttpStatus.BAD_REQUEST);
        }
        
    }

    @GetMapping(value = {"/customers/list", "/api/customers/list"})
    public List<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping(value = {"/customers/view/{email}", "/api/customers/view/{email}"})
    public Customer getCustomerByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @DeleteMapping(value = {"/customers/delete/{email}", "/api/customers/delete/{email}"})
    public ResponseEntity<Void> deleteCustomer(@PathVariable String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = {"/editcustomer/{email}", "/api/editcustomer/{email}"})
    public ResponseEntity<Customer> updateCustomer(@PathVariable String email, @RequestBody Customer customer) {
        return service.updateByEmail(email, customer);
    }


}
