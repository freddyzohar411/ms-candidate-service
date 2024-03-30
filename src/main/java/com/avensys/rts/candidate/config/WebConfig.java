package com.avensys.rts.candidate.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

@Configuration
public class WebConfig {
	@Bean
	public RequestContextListener requestContextListener(){
		return new RequestContextListener();
	}

	@Bean
	public FilterRegistrationBean<RequestContextFilter> requestContextFilter(){
		FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
		RequestContextFilter filter = new RequestContextFilter();
		registrationBean.setFilter(filter);
		return registrationBean;
	}

}
