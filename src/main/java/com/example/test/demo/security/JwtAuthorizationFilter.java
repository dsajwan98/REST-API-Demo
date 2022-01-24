package com.example.test.demo.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.test.demo.service.ApplicationUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    public ApplicationUserService applicationUserService;

    @Autowired
    public JwtTokenUtil jwtTokenUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
                //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        if(request.getServletPath().equals("/api/signin") || request.getServletPath().equals("/api/register")
                || request.getServletPath().equals("/h2-console")) {
            chain.doFilter(request, response);
        } else {
            logger.debug("Authentication Request For '{}'", request.getRequestURL());

            final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            String username = null;
            String jwtToken = null;
            JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    logger.error("JWT_TOKEN_UNABLE_TO_GET_USERNAME", e);
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT_TOKEN_EXPIRED", e);
                }
            } else {
                logger.warn("JWT_TOKEN_DOES_NOT_START_WITH_BEARER_STRING");
            }

            logger.debug("JWT_TOKEN_USERNAME_VALUE '{}'", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                ApplicationUserService applicationUserService = new ApplicationUserService();
                UserDetails userDetails = applicationUserService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            chain.doFilter(request, response);
        }
		
	}
    
}
