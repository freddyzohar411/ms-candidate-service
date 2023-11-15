package com.avensys.rts.candidate.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.candidate.constant.MessageConstants;
import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.payloadrequest.CandidateRequest;
import com.avensys.rts.candidate.service.CandidateService;
import com.avensys.rts.candidate.service.CandidateServiceImpl;
import com.avensys.rts.candidate.util.ResponseUtil;

/**
 * @author Kotaiah nalleboina 
 * This class used to get/save/update/delete candidate operations
 *         
 * 
 */
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

	private static final Logger LOG = LoggerFactory.getLogger(CandidateController.class);

	@Autowired
	private CandidateServiceImpl candidateService;
	@Autowired
	private MessageSource messageSource;
	

	/**
	 * This method is used to save Candidate Data
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> saveCandidateData(@RequestHeader Map<String, String> headers,
			@RequestBody CandidateRequest candidateRequest) {
		LOG.info("saveCandidateData request received");
		System.out.println("saveCandidateData :  "+candidateRequest);

		CandidateEntity candidateEntity = candidateService.saveCandidateData(candidateRequest);
		return ResponseUtil.generateSuccessResponse(candidateEntity, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));

	}
	/**
	 * This method is used to update candidate data
	 * @param headers
	 * @param id
	 * @param candidateRequest
	 * @return
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCandidateData(@RequestHeader Map<String, String> headers, 
			@PathVariable Integer id, @RequestBody CandidateRequest candidateRequest) {
		LOG.info("updateCandidateData request received");
		CandidateEntity candidateEntity = candidateService.updateCandidateData(id, candidateRequest);
		return ResponseUtil.generateSuccessResponse(candidateEntity, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_UPDATED, null, LocaleContextHolder.getLocale()));
	}
	/**
	 * This method is used to delete candidate data
	 * @param headers
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCandidateData(@RequestHeader Map<String, String> headers, @PathVariable Integer id) {
		LOG.info("deleteCandidateData request received");
		candidateService.deleteCandidateData(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_DELETED, null, LocaleContextHolder.getLocale()));
	}
	/**
	 * This method is used to retrieve a  candidate Information
	 * @param headers
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getCandidateData(@RequestHeader Map<String, String> headers, @PathVariable Integer id) {
		LOG.info("getCandidateData request received");
		CandidateEntity candidateEntity = candidateService.getCandidateData(id);
		return ResponseUtil.generateSuccessResponse(candidateEntity, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	
	@GetMapping
	public ResponseEntity<?> getAllCandidateData(@RequestHeader Map<String, String> headers) {
		LOG.info("getCandidateData request received");
		List<CandidateEntity> candidateEntityList = candidateService.getAllCandidateData();
		return ResponseUtil.generateSuccessResponse(candidateEntityList, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	

}
