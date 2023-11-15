package com.avensys.rts.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.candidate.entity.CandidateEntity;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Integer> , JpaSpecificationExecutor<CandidateEntity>{
	
	
	  @Query(value =
	  "SELECT c FROM candidate c WHERE c.id = ?1 AND c.isDeleted = ?2")
	  Optional<CandidateEntity> findByIdAndIsDeleted(Integer id, boolean
	  isDeleted);
	  
	  @Query(value = "SELECT c FROM candidate c WHERE c.isDeleted = ?1")
	  List<CandidateEntity> findAllAndIsDeleted(boolean isDeleted);
	 
//	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDeleted = ?2")
//	List<CandidateEntity> findAllByUserAndDeleted(Integer createdBy, boolean isDeleted);
//	
//	@Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM candidate c WHERE c.name = ?1 AND c.isDeleted = false")
//	boolean existByName(String name);
//	
//	@Query(value = "SELECT c FROM candidate c WHERE c.isDraft =?1 AND c.isDeleted = ?2")
//	Optional<CandidateEntity> findByDraftAndDeleted(boolean draft, boolean deleted);
//	
//	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDraft =?2 AND c.isDeleted = ?3")
//	Optional<CandidateEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted);
//	
//	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDraft =?2 AND c.isDeleted = ?3")
//	Page<CandidateEntity> findAllByPaginationAndSort(Integer createdBy, boolean draft, boolean deleted, Pageable pageable);
//	
//	Page<CandidateEntity> findAll(Specification<CandidateEntity> specification, Pageable pageable);
//	 
//	 
	 
	 
}
