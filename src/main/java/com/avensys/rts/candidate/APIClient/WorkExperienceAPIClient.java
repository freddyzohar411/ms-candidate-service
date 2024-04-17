package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.config.FeignConfiguration;
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
@FeignClient(name = "work-experience-service", url = "${api.work-experience.url}", configuration = JwtTokenInterceptor.class)
//@FeignClient(name = "work-experience-service", url = "${api.work-experience.url}", configuration = FeignConfiguration.class)
public interface WorkExperienceAPIClient {
	@DeleteMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteWorkExperienceByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);

	@GetMapping("/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse getWorkExperienceByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
