package com.osa.openstreetart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.osa.openstreetart.service.JwtService;
import com.osa.openstreetart.util.JwtAuthenticationEntryPoint;
import com.osa.openstreetart.util.JwtFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] ROUTES_WHITELIST = {
		// SWAGGER
		"/v2/api-docs",
		"/swagger-resources",
		"/swagger-resources/**",
		"/configuration/ui",
		"/configuration/security",
		"/swagger-ui.html",
		"/webjars/**",
		// API
		"/api/register"
	};

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthEntryPoint;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().authorizeRequests()
				.antMatchers(ROUTES_WHITELIST).permitAll()
				.anyRequest().authenticated().and()
				.exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		httpSecurity.cors();
	}
}
