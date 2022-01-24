package com.example.test.demo.repository;

import com.example.test.demo.model.Customer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Customer findTopByEmail(String email);

    Customer deleteTopByEmail(String email);
}
