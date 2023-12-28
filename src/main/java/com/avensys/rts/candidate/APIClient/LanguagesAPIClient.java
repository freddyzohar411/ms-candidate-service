package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@FeignClient(name = "languages-service", url = "${api.language.url}", configuration = JwtTokenInterceptor.class)

public interface LanguagesAPIClient {
	@DeleteMapping("/languages/entity/{entityType}/{entityId}")
	CandidateResponseDTO.HttpResponse deleteLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
