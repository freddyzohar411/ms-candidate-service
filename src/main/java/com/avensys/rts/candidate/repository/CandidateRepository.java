package com.avensys.rts.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.candidate.entity.CandidateEntity;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Integer> {
	
	 Optional<CandidateEntity> findByIdAndIsDeleted(Integer id, boolean isDeleted);
	 
	 @Query(value = "SELECT a FROM candidate a WHERE a.isDeleted = ?1")
	 List<CandidateEntity> findAllAndIsDeleted(boolean isDeleted);
}
