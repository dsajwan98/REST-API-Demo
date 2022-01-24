package com.example.test.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import com.example.test.demo.model.ApplicationUser;
import com.example.test.demo.model.ResponseObject;
import com.example.test.demo.repository.ApplicationUserRepository;
import com.example.test.demo.util.SpringUtils;


@Service
public class ApplicationUserService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository == null) {
            userRepository= (ApplicationUserRepository)SpringUtils.ctx.getBean(ApplicationUserRepository.class);
        }
        ApplicationUser user = userRepository.findTopByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("User NOT FOUND");
        } else {
            System.out.print("USER FOUND"); 
        }
        return new User(user.getUsername(), user.getPassword(), new ArrayList<SimpleGrantedAuthority>());
    }


    public ResponseObject saveUser(ApplicationUser user) {
        String success = "Registration successful";
        String failure = "Password or username policy failed";
        ApplicationUser createdUser = new ApplicationUser();
        ResponseObject response = new ResponseObject();
        
        if (userRepository.findAll().size() == 0) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            createdUser = userRepository.save(user);
            response.setMessage(success);
        } else {
            ApplicationUser savedUser = userRepository.findTopByUsername(user.getUsername());
            if (savedUser==null || !user.getUsername().equals(savedUser.getUsername())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                createdUser = userRepository.save(user);
                response.setMessage(success);
            } else {
                response.setMessage(failure);
            }
        }
        return response;
    }

}

