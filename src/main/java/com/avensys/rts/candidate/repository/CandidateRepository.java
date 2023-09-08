package com.avensys.rts.candidate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.candidate.entity.CandidateEntity;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Integer> {
	
	 Optional<CandidateEntity> findByIdAndIsDeleted(Integer id, boolean isDeleted);
}
