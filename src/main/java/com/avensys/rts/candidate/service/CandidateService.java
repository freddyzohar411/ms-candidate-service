package com.avensys.rts.candidate.service;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.payloadrequest.CandidateRequest;

public interface CandidateService {

	/**
	 * This method is used to save Candidate Data
	 * @param candidateRequest
	 * @return
	 */
	public CandidateEntity saveCandidateData(CandidateRequest candidateRequest);

	/**
	 * This method is used to retrieve a  candidate Information
	 * @param id
	 * @return
	 */
	public CandidateEntity getCandidateData(Integer id);

	/**
	 * This method is used to update candidate data
	 * @param id
	 * @param candidateRequest
	 * @return
	 */
	public CandidateEntity updateCandidateData(Integer id, CandidateRequest candidateRequest);

	/**
	 * This method is used to delete candidate data
	 * @param id
	 */
	public void deleteCandidateData(Integer id);

	
}

	
