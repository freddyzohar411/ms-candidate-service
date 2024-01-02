package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@FeignClient(name = "employer-details-service", url = "${api.employer-details.url}", configuration = JwtTokenInterceptor.class)

public interface EmployerDetailsAPIClient {
	@DeleteMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteEmployerDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
