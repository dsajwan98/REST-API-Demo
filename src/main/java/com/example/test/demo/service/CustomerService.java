package com.example.test.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.test.demo.model.Customer;
import com.example.test.demo.model.ResponseObject;
import com.example.test.demo.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public ResponseObject saveCustomer(Customer customer) {
        String success = "Registration successful";
        String failure = "Registration failure";
        ResponseObject response = new ResponseObject();        
    
        if(customerRepository.findAll().size() == 0 || customerRepository.findTopByEmail(customer.getEmail()) == null) {
            Customer savedCustomer = customerRepository.save(customer);
            if (savedCustomer!=null) {
                response.setMessage(success);
            }
        } else {
            response.setMessage(failure);
        }
        return response;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findByEmail(String email) {
        return customerRepository.findTopByEmail(email);
    }

    public void deleteByEmail(String email) {
        customerRepository.deleteTopByEmail(email);
    }

    public ResponseEntity<Customer> updateByEmail(String email, Customer customer) {
        Customer newCustomer = customerRepository.findTopByEmail(email);
        newCustomer.setName(customer.getName());
        newCustomer.setGender(customer.getGender());
        newCustomer.setMobile(customer.getMobile());
        newCustomer.setDob(customer.getDob());
        Customer updatedCustomer = customerRepository.save(newCustomer);
        return ResponseEntity.ok(updatedCustomer);
    }

}
