package com.avensys.rts.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.candidate.entity.CandidateNewEntity;

public interface CandidateNewRepository extends JpaRepository<CandidateNewEntity,Integer>,CustomCandidateRepository {
	@Query(value = "SELECT c FROM candidateNew c WHERE c.id = ?1 AND c.isDeleted = ?2")
	Optional<CandidateNewEntity> findByIdAndDeleted(int id, boolean isDeleted);
	
	@Query(value = "SELECT c FROM candidateNew c WHERE c.createdBy = ?1 AND c.isDraft = ?2 AND c.isDeleted = ?3")
	Optional<CandidateNewEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted);
	
	@Query(value = "SELECT c FROM candidateNew c WHERE c.createdBy = ?1 AND c.isDraft = ?2 AND c.isDeleted = ?3")
    List<CandidateNewEntity> findAllByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted);
	
	//@Query(value = "SELECT c FROM candidateNew c WHERE c.createdBy = ?1 AND c.isDeleted = ?2")
	//List<CandidateNewEntity> findAllByUserAndDeleted(Integer createdBy, boolean isDeleted);
	
	@Query(value = "SELECT c FROM candidateNew c WHERE c.id = ?1 AND c.isDraft = ?2")
	Optional<CandidateNewEntity> findByIdAndDraft(Integer id, boolean draft);
	
	

}
