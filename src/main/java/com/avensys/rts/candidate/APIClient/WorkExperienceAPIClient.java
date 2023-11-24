package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

@Configuration
@FeignClient(name = "work-experience-service", url = "http://localhost:9500", configuration = JwtTokenInterceptor.class)
public interface WorkExperienceAPIClient {
	@DeleteMapping("/work-experience/entity/{entityType}/{entityId}")
	HttpResponse deleteWorkExperienceByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);
}
