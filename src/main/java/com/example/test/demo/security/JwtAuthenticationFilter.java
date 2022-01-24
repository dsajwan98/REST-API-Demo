package com.example.test.demo.security;

import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import com.example.test.demo.model.ApplicationUser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import io.jsonwebtoken.Clock;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    @Value("${jwt.signing.key.secret}")
	private String secret;

	@Value("${jwt.token.expiration.in.seconds}")
	private Long expiration;

    private Clock clock = DefaultClock.INSTANCE;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        
        ObjectMapper mapper = new ObjectMapper();
        ApplicationUser user = new ApplicationUser();
        try {
            byte[] bytes = request.getInputStream().readAllBytes();

            String base64Encoded = DatatypeConverter.printBase64Binary(bytes);
            System.out.println("Encoded Json:\n");
            System.out.println(base64Encoded + "\n");

            byte[] base64Decoded = DatatypeConverter.parseBase64Binary(base64Encoded);
            System.out.println("Decoded Json:\n");
            System.out.println(new String(base64Decoded));
            user = mapper.readValue(base64Decoded, ApplicationUser.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);
        User user = (User)authResult.getPrincipal();
        String access_token = Jwts.builder().setSubject(user.getUsername()).setIssuedAt(createdDate)
				.setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, "HRlELXqpSB").compact();
        Map<String, Object> res = new HashMap<>();
        if(access_token != null) {
            res.put("message", "Authentication Successful!");
            res.put("token", access_token);
            res.put("id", user.getUsername());
        } 
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), res);
    }

    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Username or Password is Incorrect");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), res);

    }

    private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + 600000);
	}
    
    
}
