package com.avensys.rts.candidate.APIClient;

import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;

@FeignClient(name = "user-service", url = "http://localhost:8090/api/user", configuration = JwtTokenInterceptor.class)
public interface UserAPIClient {
	@GetMapping("/{id}")
	CandidateResponseDTO.HttpResponse getUserById(@PathVariable("id") Integer id);
	
	//@GetMapping("")
    //HttpResponse getUserByEmail(@RequestParam(required = false) String email);
	
	 @GetMapping("/email/{email}")
	 CandidateResponseDTO.HttpResponse getUserByEmail(@PathVariable("email") String email);
	 
	 @GetMapping("/{id}")
	 CandidateResponseDTO.HttpResponse find(@PathVariable("id") Long id);

	
//	@GetMapping("/{id}")
//	public ResponseEntity<?> find(@PathVariable("id") Long id);
	 
	 @GetMapping("/profile")
	 CandidateResponseDTO.HttpResponse getUserDetail();

	@GetMapping("/users-under-manager")
	CandidateResponseDTO.HttpResponse getUsersUnderManager();
	
}
