package com.jsp.ojpms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jsp.ojpms.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {

		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		AuthenticationEntryPoint authenticationEntryPoint = (request, response, exception) -> {
			response.setStatus(401);
			response.setContentType("application/json");
			response.getWriter().write("{\"status\":401,\"message\":\"Authentication required\"}");
		};

		AccessDeniedHandler accessDeniedHandler = (request, response, exception) -> {
			response.setStatus(403);
			response.setContentType("application/json");
			response.getWriter().write("{\"status\":403,\"message\":\"Access denied\"}");
		};

		http

				// ============================================
				// CSRF
				// ============================================
				.csrf(csrf -> csrf.disable())

				// ============================================
				// CORS
				// ============================================
				.cors(cors -> {
				})

				// ============================================
				// JWT = STATELESS
				// ============================================
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// ============================================
				// SECURITY ERROR HANDLING
				// ============================================
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))

				// ============================================
				// AUTHORIZATION
				// ============================================
				.authorizeHttpRequests(auth -> auth

						// ----------------------------------------
						// PUBLIC - REGISTRATION
						// ----------------------------------------
						.requestMatchers(HttpMethod.POST, "/api/users").permitAll()

						// ----------------------------------------
						// PUBLIC - LOGIN
						// ----------------------------------------
						.requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()

						// ----------------------------------------
						// PUBLIC - VIEW JOBS
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/jobs").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/jobs/*").permitAll()

						// ----------------------------------------
						// PUBLIC - SEARCH JOBS
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/jobs/search/**").permitAll()

						// ----------------------------------------
						// RECRUITER - CREATE JOB
						// ----------------------------------------
						.requestMatchers(HttpMethod.POST, "/api/jobs").hasRole("RECRUITER")

						// ----------------------------------------
						// RECRUITER - UPDATE JOB
						// Ownership checked in JobService
						// ----------------------------------------
						.requestMatchers(HttpMethod.PUT, "/api/jobs/*").hasRole("RECRUITER")

						// ----------------------------------------
						// RECRUITER - DELETE JOB
						// Ownership checked in JobService
						// ----------------------------------------
						.requestMatchers(HttpMethod.DELETE, "/api/jobs/*").hasRole("RECRUITER")

						// ----------------------------------------
						// RECRUITER - MY JOBS
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/jobs/recruiter/my-jobs").hasRole("RECRUITER")

						// ----------------------------------------
						// JOB SEEKER - APPLY
						// ----------------------------------------
						.requestMatchers(HttpMethod.POST, "/api/applications").hasRole("JOB_SEEKER")

						// ----------------------------------------
						// JOB SEEKER - MY APPLICATIONS
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/applications/my-applications").hasRole("JOB_SEEKER")

						// ----------------------------------------
						// RECRUITER - MY APPLICATIONS
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/applications/my-recruiter-applications")
						.hasRole("RECRUITER")

						// ----------------------------------------
						// RECRUITER - APPLICATIONS FOR JOB
						// Ownership checked in service
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/applications/job/*").hasRole("RECRUITER")

						// ----------------------------------------
						// RECRUITER - UPDATE APPLICATION STATUS
						// Ownership checked in service
						// ----------------------------------------
						.requestMatchers(HttpMethod.PUT, "/api/applications/*/status").hasRole("RECRUITER")

						// ----------------------------------------
						// APPLICATION BY ID
						// Service checks ownership
						// ----------------------------------------
						.requestMatchers(HttpMethod.GET, "/api/applications/*").authenticated()

						// ----------------------------------------
						// DELETE APPLICATION
						// Service checks applicant ownership
						// ----------------------------------------
						.requestMatchers(HttpMethod.DELETE, "/api/applications/*").hasRole("JOB_SEEKER")

						// ----------------------------------------
						// EVERYTHING ELSE
						// ----------------------------------------
						.anyRequest().authenticated())

				// ============================================
				// JWT FILTER
				// ============================================
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}