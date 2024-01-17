package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadnewrequest.FormSubmissionsRequestDTO;

@FeignClient(name = "form-service", url = "${api.form-submission.url}", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {
	@PostMapping("")
	CandidateResponseDTO.HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);
	
	@GetMapping("/{formSubmissionId}")
	CandidateResponseDTO.HttpResponse getFormSubmission(@PathVariable int formSubmissionId);
	
	@PutMapping("/{formSubmissionId}")
	CandidateResponseDTO.HttpResponse updateFormSubmission(@PathVariable int formSubmissionId, @RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@DeleteMapping("/{formSubmissionId}")
	CandidateResponseDTO.HttpResponse deleteFormSubmission(@PathVariable int formSubmissionId);

	@GetMapping("/entity/{entityName}/names")
	CandidateResponseDTO.HttpResponse getFormFieldNameList(@PathVariable String entityName);

}
