package com.avensys.rts.candidate.config;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration{

	@Bean
	public JwtTokenInterceptor jwtTokenInterceptor() {
		return new JwtTokenInterceptor();
		// Ensure JwtTokenInterceptor itself does not hold mutable shared state
	}
}