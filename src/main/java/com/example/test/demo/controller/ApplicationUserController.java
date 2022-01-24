package com.example.test.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.test.demo.model.ApplicationUser;
import com.example.test.demo.model.LoginResponse;
import com.example.test.demo.model.ResponseObject;
import com.example.test.demo.repository.ApplicationUserRepository;
import com.example.test.demo.security.JwtTokenUtil;
import com.example.test.demo.service.ApplicationUserService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ApplicationUserController {
    @Autowired
	private AuthenticationManager authenticationManager;

    @Autowired 
    private ApplicationUserService service;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
	private JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = {"/register", "/api/register"})
    public ResponseEntity<ResponseObject> registerUser(@RequestBody ApplicationUser user) {
        String success = "Registration successful";
        //String failure = "Password or username policy failed";
        ApplicationUser createdUser = new ApplicationUser();
        ResponseObject response = service.saveUser(user);
        if (response.getMessage().equals(success)) {
            return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ResponseObject>(response, HttpStatus.BAD_REQUEST);
        }
        
    }

    @RequestMapping(value = {"/refresh", "/api/refresh"}, method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String token = authToken.substring(7);
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

		if (jwtTokenUtil.canTokenBeRefreshed(token)) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
            Map<String, Object> res = new HashMap<>();
            if(refreshedToken != null) {
                res.put("message", "Token Refreshed!");
                res.put("token", refreshedToken);
            } 
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), res);
			return ResponseEntity.ok(res);
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

    @RequestMapping(value = {"/viewprofile/{username}", "/api/viewprofile/{username}"}, method = RequestMethod.GET)
    public ApplicationUser getUserById(@PathVariable String username) {
        return userRepository.findTopByUsername(username);
    }

    @RequestMapping(value = {"/editprofile/{username}", "/api/editprofile/{username}"}, method = RequestMethod.PUT)
    public ResponseEntity<ApplicationUser> updateUser(@PathVariable String username, @RequestBody ApplicationUser user) {
        ApplicationUser newUser = userRepository.findTopByUsername(username);
        newUser.setMobile(user.getMobile());
        newUser.setEmail(user.getEmail());
        newUser.setLocation(user.getLocation());
        ApplicationUser updatedUser = userRepository.save(newUser);
        return ResponseEntity.ok(updatedUser);
    }

    @RequestMapping(value = "/api/signin", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody ApplicationUser applicationUser)
			throws AuthenticationException {
        try {
            authenticate(applicationUser.getUsername(), applicationUser.getPassword());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseObject("Bad Request"));
        }      

		final UserDetails userDetails = service
				.loadUserByUsername(applicationUser.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);
       
		return ResponseEntity.ok(new LoginResponse("Authentication Successful!", token, applicationUser.getUsername()));
	}

    private void authenticate(String username, String password) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
	
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		
	}
}
