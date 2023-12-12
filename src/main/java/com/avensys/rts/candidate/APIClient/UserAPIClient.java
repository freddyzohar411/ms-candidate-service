package com.avensys.rts.candidate.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;

import java.util.Set;

@FeignClient(name = "user-service", url = "http://localhost:8090/api/user", configuration = JwtTokenInterceptor.class)
public interface UserAPIClient {
	@GetMapping("/{id}")
    HttpResponse getUserById(@PathVariable("id") Integer id);
	
	//@GetMapping("")
    //HttpResponse getUserByEmail(@RequestParam(required = false) String email);
	
	 @GetMapping("/email/{email}")
	 HttpResponse getUserByEmail(@PathVariable("email") String email);
	 
	 @GetMapping("/{id}")
	 HttpResponse find(@PathVariable("id") Long id);

	
//	@GetMapping("/{id}")
//	public ResponseEntity<?> find(@PathVariable("id") Long id);
	 
	 @GetMapping("/profile")
	 HttpResponse getUserDetail();

	@GetMapping("/users-under-manager")
	HttpResponse getUsersUnderManager();
	
}
