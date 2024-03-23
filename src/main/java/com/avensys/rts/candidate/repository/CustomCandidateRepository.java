package com.avensys.rts.candidate.repository;



import java.util.List;
import java.util.Set;

import com.avensys.rts.candidate.entity.CandidateEntityWithSimilarity;
import com.avensys.rts.candidate.payloadnewresponse.CandidateJobSimilaritySearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.avensys.rts.candidate.entity.CandidateEntity;

public interface CustomCandidateRepository {
	
	// Not used
	  Page<CandidateEntity>findAllByOrderBy(Integer userId, Boolean isDeleted,
	  Boolean isDraft,Boolean isActive,Pageable pageable);
	  
	  Page<CandidateEntity>findAllByOrderByString(Integer userId, Boolean
	  isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);
	  
	  Page<CandidateEntity>findAllByOrderByNumeric(Integer userId, Boolean
	  isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable);
	  
	  Page<CandidateEntity>findAllByOrderByAndSearchString(Integer userId,
	  Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
	  searchFields, String searchTerm);
	  
	  Page<CandidateEntity>findAllByOrderByAndSearchNumeric(Integer userId,
	  Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
	  searchFields, String searchTerm);

	  // With user groups
	  Page<CandidateEntity>findAllByOrderByStringWithUserGroups(Set<Long> userGroupIds, Boolean
			  isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateEntity>findAllByOrderByNumericWithUserGroups(Set<Long> userGroupIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateEntity>findAllByOrderByAndSearchStringWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
			searchFields, String searchTerm);

	Page<CandidateEntity>findAllByOrderByAndSearchNumericWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
			searchFields, String searchTerm);

	// Check only user id
	Page<CandidateEntity>findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateEntity>findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateEntity>findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds,
			Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
			searchFields, String searchTerm);

	Page<CandidateEntity>findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
			searchFields, String searchTerm);

	void insertVector(Long candidateId, String columnName, List<Float> vector);

	void updateVector(Long candidateId, String columnName, List<Float> vector);

	List<CandidateJobSimilaritySearchResponseDTO> findSimilarEmbeddingsCosine(List<Float> targetVector, String columnName);

	public List<CandidateJobSimilaritySearchResponseDTO> findSimilarSumScoresWithJobDescription(List<Float> jobDescriptionVector);

	Page<CandidateEntityWithSimilarity>findAllByOrderByStringWithUserIdsAndSimilaritySearch(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable, List<Float> jobDescriptionVector);

	Page<CandidateEntity>findAllByOrderByNumericWithUserIdsAndSimilaritySearch(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable, List<Float> jobDescriptionVector);
}
