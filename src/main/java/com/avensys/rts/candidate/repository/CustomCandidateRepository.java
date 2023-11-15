package com.avensys.rts.candidate.repository;



import java.util.List;
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
	 
}
