package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

@FeignClient(name = "certification-service", url = "http://localhost:9700", configuration = JwtTokenInterceptor.class)

public interface CertificationAPIClient {
	@DeleteMapping("/certifications/entity/{entityType}/{entityId}")
	HttpResponse deleteCertificationsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
