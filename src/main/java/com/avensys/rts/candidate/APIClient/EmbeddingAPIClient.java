package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewrequest.EmbeddingListCompareRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.EmbeddingListTextCompareRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.EmbeddingTextCompareRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadnewrequest.EmbeddingRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;

import java.io.IOException;

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

	@PostMapping("/get/single/py")
	CandidateResponseDTO.HttpResponse getEmbeddingSinglePy(@RequestBody EmbeddingRequestDTO embeddingRequestDTO);

	@PostMapping("/compare/list")
	CandidateResponseDTO.HttpResponse compareEmbeddingsList(@RequestBody EmbeddingListCompareRequestDTO embeddingListCompareRequestDTO);

	@PostMapping("/compare/list-text")
	CandidateResponseDTO.HttpResponse compareEmbeddingsListText(@RequestBody EmbeddingListTextCompareRequestDTO embeddingListTextCompareRequestDTO);

	@PostMapping("/compare/text")
	CandidateResponseDTO.HttpResponse compareEmbeddingsText(@RequestBody EmbeddingTextCompareRequestDTO embeddingTextCompareRequestDTO);
}
