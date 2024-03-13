package com.avensys.rts.candidate.repository;

import com.avensys.rts.candidate.entity.CandidateMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateMappingRepository extends JpaRepository<CandidateMappingEntity, Integer> {
	Optional<CandidateMappingEntity> findByEntityType(String entityType);
}
