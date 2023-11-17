package com.avensys.rts.candidate.controller;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.candidate.constant.MessageConstants;
import com.avensys.rts.candidate.payloadnewrequest.CandidateNewRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateNewResponseDTO;
import com.avensys.rts.candidate.service.CandidateNewServiceImpl;
import com.avensys.rts.candidate.util.ResponseUtil;

import jakarta.validation.Valid;

/**
 * Author:Kotaiah nalleboina This is the New Controller class for candidate that
 * work with dynamic forms.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/candidates")
public class CandidateNewController {

	private final Logger LOG = LoggerFactory.getLogger(CandidateNewController.class);
	@Autowired
	private CandidateNewServiceImpl candidateNewService;
	@Autowired
	private MessageSource messageSource;

	public CandidateNewController(CandidateNewServiceImpl candidateNewService, MessageSource messageSource) {
		this.candidateNewService = candidateNewService;
		this.messageSource = messageSource;
	}

	/**
	 * This method is used to create Candidate draft
	 * 
	 * @param candidateNewRequestDTO
	 * @return
	 */
	@PostMapping("")
	public ResponseEntity<Object> addCandidate(@Valid @ModelAttribute CandidateNewRequestDTO candidateNewRequestDTO) {
		LOG.info("Candidate create: Controller");
		System.out.println("CandidateNewController: " + candidateNewRequestDTO);
		CandidateNewResponseDTO candidateNewResponseDTO = candidateNewService.createCandidate(candidateNewRequestDTO);
		return ResponseUtil.generateSuccessResponse(candidateNewResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.CANDIDATE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This method is used to retrieve Candidate Data by id
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Object>getCandidate(@PathVariable int id){
		LOG.info("Candidate get: Controller");
		 CandidateNewResponseDTO candidateNewResponseDTO = candidateNewService.getCandidate(id);
		 return ResponseUtil.generateSuccessResponse(candidateNewResponseDTO, HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	/**
	 * This method is used to update Candidate Data by id
	 * @param id
	 * @param candidateNewRequestDTO
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Object>updateCandidate(@PathVariable int id,@ModelAttribute CandidateNewRequestDTO candidateNewRequestDTO){
		LOG.info("Candidate update: Controller");
		CandidateNewResponseDTO candidateNewResponseDTO = candidateNewService.updateCandidate(id,candidateNewRequestDTO);
		return ResponseUtil.generateSuccessResponse(candidateNewResponseDTO, HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_UPDATED, null, LocaleContextHolder.getLocale()));
	}
	/**
	 * This method is used to delete draft Candidate
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delete/draft/{id}")
	public ResponseEntity<Object>deleteCandidate(@PathVariable int id){
		LOG.info("Candidate delete: Controller");
		candidateNewService.deleteDraftCandidate(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_DELETED, null, LocaleContextHolder.getLocale()));
		
	}
	/**
	 * Get candidate draft if exists
	 * @return
	 */
	@GetMapping("/draft")
	public ResponseEntity<Object>getCandidateIfDraft(){
	LOG.info("Candidate get: Controller");	
	CandidateNewResponseDTO candidateNewResponseDTO = candidateNewService.getCandidateIfDraft();
	return ResponseUtil.generateSuccessResponse(candidateNewResponseDTO, HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	
	/**
	 * Soft delete existing candidate
	 * @param id
	 * @return
	 */
	@DeleteMapping("/soft/delete/{id}")
	public ResponseEntity<Object> softDeleteCandidate (@PathVariable int id){
		LOG.info("Candidate soft delete: Controller");
		candidateNewService.softDeleteCandidate(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_DELETED, null, LocaleContextHolder.getLocale()));
	}
	

	/**
	 * Get all candidate field for all forms related to candidates
	 * @return
	 */
	@GetMapping("/fields")
	public ResponseEntity<Object>getAllCandidatesFields(){
	LOG.info("Candidate get all fields: Controller");
	return ResponseUtil.generateSuccessResponse(candidateNewService.getAllCandidatesFields(), HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
//
//	@GetMapping("/search")
//	public ResponseEntity<Object>searchCandidate(@RequestParam( value = "query",required = false)String query){
//	LOG.info("Candidate search: Controller");
//	if (query != null) {
//		System.out.println("Query: " + query);
//		String regex = "(\\w+)([><]=?|!=|=)(\\w+)";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(query);
//		
//		while (matcher.find()) {
//			String fieldName = matcher.group(1);
//            String operator = matcher.group(2);
//            String value = matcher.group(3);
//            
//            //Now you have fieldName, operator, and value for each key-value pair
//            System.out.println("Field Name: " + fieldName);
//            System.out.println("Operator: " + operator);
//            System.out.println("Value: " + value);
//		}
//	}
//	 if (query == null || query.isEmpty()) {
//		 return ResponseUtil.generateSuccessResponse(candidateNewService.getAllCandidatesByUser(false, false) ,HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
//	 }
//	 return ResponseUtil.generateSuccessResponse(candidateNewService.getAllCandidatesWithSearch(query),HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
//	}

//	
//
}
