package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;
@FeignClient(name = "form-service", url = "http://localhost:9400", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {
	@PostMapping("/form-submissions")
    HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);
	
	@GetMapping("/form-submissions/{formSubmissionId}")
	HttpResponse getFormSubmission(@PathVariable int formSubmissionId);
	
	@PutMapping("/form-submissions/{formSubmissionId}")
	HttpResponse updateFormSubmission(@PathVariable int formSubmissionId, @RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@DeleteMapping("/form-submissions/{formSubmissionId}")
	HttpResponse deleteFormSubmission(@PathVariable int formSubmissionId);
	

}
