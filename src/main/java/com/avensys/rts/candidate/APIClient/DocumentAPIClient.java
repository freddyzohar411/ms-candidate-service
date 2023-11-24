package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

/**
 * author Koh He Xiang
 * This class is an interface to interact with document microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "http://localhost:8500", configuration = JwtTokenInterceptor.class)
public interface DocumentAPIClient {
    @DeleteMapping("/documents/entity/{entityType}/{entityId}")
    HttpResponse deleteDocumentsByEntityTypeAndEntityId(@PathVariable String entityType, @PathVariable Integer entityId);
}
