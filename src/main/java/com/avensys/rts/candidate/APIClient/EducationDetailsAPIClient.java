package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

@Configuration
@FeignClient(name = "education-details-service", url = "http://localhost:9600", configuration = JwtTokenInterceptor.class)
public interface EducationDetailsAPIClient {

	@DeleteMapping("/education-details/entity/{entityType}/{entityId}")
	HttpResponse deleteEducationDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
