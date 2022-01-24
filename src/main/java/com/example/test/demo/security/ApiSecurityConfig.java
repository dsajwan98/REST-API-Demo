package com.example.test.demo.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@ComponentScan
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authenticationManagerBean());
        filter.setFilterProcessesUrl("/signin");
        http.cors().configurationSource(corsConfigurationSource());
        http.csrf().disable();
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/signin").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/signin").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/register").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/register").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/refresh").permitAll();
        http.authorizeRequests().antMatchers("/h2-console/**/**").permitAll();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(filter);
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTION","DELETE","PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        //source.registerCorsConfiguration("/signin", configuration);
        return source;
    }

    @Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring()
                .antMatchers(HttpMethod.POST, "/register /signin /api/register /api/signin")
				.antMatchers(HttpMethod.OPTIONS, "/**")
                // .antMatchers(HttpMethod.POST, "/signin")
				// .antMatchers(HttpMethod.OPTIONS, "/**")
				.and().ignoring()
				.antMatchers(HttpMethod.GET, "/").and().ignoring()
				.antMatchers("/h2-console/**/**");// Should not be done in Production!
	}

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }
    
}
