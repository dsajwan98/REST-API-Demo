package com.example.test.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class ApplicationUser {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String mobile;
    private String location;
}
