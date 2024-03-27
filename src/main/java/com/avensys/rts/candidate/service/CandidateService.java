package com.avensys.rts.candidate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.entity.CandidateEntityWithSimilarity;
import com.avensys.rts.candidate.model.FieldInformation;
import com.avensys.rts.candidate.payloadnewrequest.CandidateJobSimilaritySearchRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.CandidateListingRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.*;
import com.avensys.rts.candidate.payloadnewrequest.CandidateRequestDTO;
import org.springframework.data.domain.Page;

public interface CandidateService {
	CandidateResponseDTO createCandidate(CandidateRequestDTO candidateRequestDTO);
	
	CandidateResponseDTO getCandidate(Integer id);
	
	CandidateResponseDTO getCandidateIfDraft();
	
	CandidateResponseDTO updateCandidate(Integer id, CandidateRequestDTO candidateRequestDTO);
	
	Set<FieldInformation>getAllCandidatesFields();
	
	void deleteDraftCandidate (Integer id);
	
	void softDeleteCandidate(Integer id);
	
	CandidateListingResponseDTO getCandidateListingPage(Integer page, Integer size, String sortBy, String sortDirection, Boolean getAll);
	
	CandidateListingResponseDTO getCandidateListingPageWithSearch(Integer page, Integer size, String sortBy, String sortDirection, String searchTerm, List<String>searchFields, Boolean getAll);

	CandidateSimilarityListingResponseDTO getCandidateListingPageWithSimilaritySearch(CandidateListingRequestDTO candidateListingRequestDTO)
			throws ExecutionException, InterruptedException;

	CandidateSimilarityListingResponseDTO getCandidateListingPageWithSimilaritySearchOpenai(CandidateListingRequestDTO candidateListingRequestDTO)
			throws ExecutionException, InterruptedException;

	//List<CandidateNewEntity>getAllCandidatesWithSearch(String query);
	
	List<CandidateEntity>getAllCandidatesByUser(boolean draft, boolean deleted);

	CandidateResponseDTO completeCandidateCreate(Integer id);

	CandidateListingDataDTO getCandidateByIdData(Integer candidateId);

	HashMap<String, Object> getCandidateByIdDataAll(Integer candidateId);

	HashMap<String, List<HashMap<String, String>>> getAllCandidatesFieldsAll();

	HashMap<String, Object> updateCandidateEmbeddings(Integer candidateId);

	List<CandidateJobSimilaritySearchResponseDTO> getCandidateJobSimilaritySearch(
			CandidateJobSimilaritySearchRequestDTO candidateJobSimilaritySearchRequestDTO);

}
