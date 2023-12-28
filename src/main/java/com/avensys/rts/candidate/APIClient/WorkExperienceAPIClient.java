package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@Configuration
@FeignClient(name = "work-experience-service", url = "${api.work-experience.url}", configuration = JwtTokenInterceptor.class)
public interface WorkExperienceAPIClient {
	@DeleteMapping("/work-experience/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteWorkExperienceByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
