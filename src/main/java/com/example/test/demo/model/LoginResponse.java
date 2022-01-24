package com.example.test.demo.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginResponse implements Serializable{

    private final String message;
	private final String token;
    private final String id;
	
}
