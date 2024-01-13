package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@FeignClient(name = "certification-service", url = "${api.certification.url}", configuration = JwtTokenInterceptor.class)

public interface CertificationAPIClient {
	@DeleteMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteCertificationsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);

	@GetMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse getCertificationsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
