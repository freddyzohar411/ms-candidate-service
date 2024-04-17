package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;

@FeignClient(name = "job-service", url = "${api.job.url}", configuration = JwtTokenInterceptor.class)

public interface JobAPIClient {
	@GetMapping("/{jobId}/data/all")
	CandidateResponseDTO.HttpResponse getJobByIdDataAll(@PathVariable Long jobId);

	@GetMapping("/{jobId}/embeddings/get/{type}")
	CandidateResponseDTO.HttpResponse getEmbeddingsById(@PathVariable Long jobId, @PathVariable String type);
}
