package com.example.test.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Customer {
    @Id
    private String id;
    private String name;
    private String gender;
    private String dob;

    @Indexed(unique = true)
    private String email;
    private String mobile;
}
