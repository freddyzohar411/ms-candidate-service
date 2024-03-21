package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadnewrequest.EmbeddingRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;

/**
 * @author Rahul Sahu
 * @description This class is an interface to interact with document
 *              microservice
 */
@Configuration
@FeignClient(name = "embedding-service", url = "${api.embeddings.url}", configuration = JwtTokenInterceptor.class)
public interface EmbeddingAPIClient {

	@PostMapping("/get/single")
	CandidateResponseDTO.HttpResponse getEmbeddingSingle(@RequestBody EmbeddingRequestDTO embeddingRequestDTO);
}
