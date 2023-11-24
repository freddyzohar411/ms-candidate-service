package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

@FeignClient(name = "languages-service", url = "http://localhost:9800", configuration = JwtTokenInterceptor.class)

public interface LanguagesAPIClient {
	@DeleteMapping("/languages/entity/{entityType}/{entityId}")
	HttpResponse deleteLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
