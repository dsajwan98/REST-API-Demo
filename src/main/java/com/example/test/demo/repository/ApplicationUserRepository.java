package com.example.test.demo.repository;

import com.example.test.demo.model.ApplicationUser;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationUserRepository extends MongoRepository<ApplicationUser, String> {
   ApplicationUser findTopByUsername(String username);
}
