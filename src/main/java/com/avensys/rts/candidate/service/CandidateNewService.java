package com.avensys.rts.candidate.service;

import java.util.List;
import java.util.Set;

import com.avensys.rts.candidate.entity.CandidateNewEntity;
import com.avensys.rts.candidate.model.FieldInformation;
import com.avensys.rts.candidate.payloadnewrequest.CandidateNewRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateListingNewResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateNewResponseDTO;

public interface CandidateNewService {
	CandidateNewResponseDTO createCandidate(CandidateNewRequestDTO candidateNewRequestDTO);
	
	CandidateNewResponseDTO getCandidate(Integer id);
	
	CandidateNewResponseDTO getCandidateIfDraft();
	
	CandidateNewResponseDTO updateCandidate(Integer id,CandidateNewRequestDTO candidateNewRequestDTO);
	
	Set<FieldInformation>getAllCandidatesFields();
	
	void deleteDraftCandidate (Integer id);
	
	void softDeleteCandidate(Integer id);
	
	CandidateListingNewResponseDTO getCandidateListingPage(Integer page, Integer size, String sortBy, String sortDirection);
	
	CandidateListingNewResponseDTO getCandidateListingPageWithSearch(Integer page, Integer size, String sortBy, String sortDirection, String searchTerm, List<String>searchFields);
	
	//List<CandidateNewEntity>getAllCandidatesWithSearch(String query);
	
	List<CandidateNewEntity>getAllCandidatesByUser(boolean draft, boolean deleted);

}
