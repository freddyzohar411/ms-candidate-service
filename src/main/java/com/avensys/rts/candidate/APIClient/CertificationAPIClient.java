package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@FeignClient(name = "certification-service", url = "${api.certification.url}", configuration = JwtTokenInterceptor.class)

public interface CertificationAPIClient {
	@DeleteMapping("/certifications/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteCertificationsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
