package com.avensys.rts.candidate.repository;



import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.avensys.rts.candidate.entity.CandidateNewEntity;

public interface CustomCandidateRepository {
	
	// Not used
	  Page<CandidateNewEntity>findAllByOrderBy(Integer userId, Boolean isDeleted,
	  Boolean isDraft,Boolean isActive,Pageable pageable);
	  
	  Page<CandidateNewEntity>findAllByOrderByString(Integer userId, Boolean
	  isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);
	  
	  Page<CandidateNewEntity>findAllByOrderByNumeric(Integer userId, Boolean
	  isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable);
	  
	  Page<CandidateNewEntity>findAllByOrderByAndSearchString(Integer userId,
	  Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
	  searchFields, String searchTerm);
	  
	  Page<CandidateNewEntity>findAllByOrderByAndSearchNumeric(Integer userId,
	  Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
	  searchFields, String searchTerm);

	  // With user groups
	  Page<CandidateNewEntity>findAllByOrderByStringWithUserGroups(Set<Long> userGroupIds, Boolean
			  isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateNewEntity>findAllByOrderByNumericWithUserGroups(Set<Long> userGroupIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateNewEntity>findAllByOrderByAndSearchStringWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
			searchFields, String searchTerm);

	Page<CandidateNewEntity>findAllByOrderByAndSearchNumericWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
			searchFields, String searchTerm);

	// Check only user id
	Page<CandidateNewEntity>findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateNewEntity>findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean
			isDeleted, Boolean isDraft,Boolean isActive,Pageable pageable);

	Page<CandidateNewEntity>findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds,
			Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable,List<String>
			searchFields, String searchTerm);

	Page<CandidateNewEntity>findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive,Pageable pageable,List<String>
			searchFields, String searchTerm);
}
