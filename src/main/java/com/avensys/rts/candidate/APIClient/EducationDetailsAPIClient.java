package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@Configuration
@FeignClient(name = "education-details-service", url = "${api.education-details.url}", configuration = JwtTokenInterceptor.class)
public interface EducationDetailsAPIClient {

	@DeleteMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteEducationDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);

	@GetMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse getEducationDetailsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
