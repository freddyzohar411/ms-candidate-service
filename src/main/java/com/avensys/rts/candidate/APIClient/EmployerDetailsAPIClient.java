package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

@FeignClient(name = "employer-details-service", url = "http://localhost:9900", configuration = JwtTokenInterceptor.class)

public interface EmployerDetailsAPIClient {
	@DeleteMapping("/employer-details/entity/{entityType}/{entityId}")
	HttpResponse deleteEmployerDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
