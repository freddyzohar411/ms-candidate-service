package com.avensys.rts.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.candidate.entity.CandidateEntity;
import org.springframework.data.repository.query.Param;

public interface CandidateRepository extends JpaRepository<CandidateEntity,Integer>,CustomCandidateRepository {
	@Query(value = "SELECT c FROM candidate c WHERE c.id = ?1 AND c.isDeleted = ?2 AND c.isActive = ?3")
	Optional<CandidateEntity> findByIdAndDeleted(int id, boolean isDeleted, boolean isActive);
	
	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDraft = ?2 AND c.isDeleted = ?3 AND c.isActive = ?4")
	Optional<CandidateEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted, boolean isActive);
	
	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDraft = ?2 AND c.isDeleted = ?3 AND c.isActive = ?4")
    List<CandidateEntity> findAllByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted, boolean isActive);
	
	@Query(value = "SELECT c FROM candidate c WHERE c.createdBy = ?1 AND c.isDeleted = ?2 AND c.isActive = ?3")
	List<CandidateEntity> findAllByUserAndDeleted(Integer createdBy, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT a FROM candidate a WHERE a.createdBy IN (?1) AND a.isDeleted = ?2 AND a.isActive = ?3")
	List<CandidateEntity> findAllByUserIdsAndDeleted(List<Long> createdByList, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT c FROM candidate c WHERE c.id = ?1 AND c.isDraft = ?2 AND c.isActive = ?3")
	Optional<CandidateEntity> findByIdAndDraft(Integer id, boolean draft, boolean isActive);

	// Check if email exists in candidate_submission_data.email
	@Query(value = "SELECT EXISTS(" +
			"SELECT 1 FROM candidate " +
			"WHERE candidate_submission_data ->> 'email' = :email)", nativeQuery = true)
	Boolean existsByEmail(@Param("email") String email);

	// Check if email exists in candidate_submission_data.email and is not deleted
	@Query(value = "SELECT EXISTS(" +
			"SELECT 1 FROM candidate " +
			"WHERE candidate_submission_data ->> 'email' = :email AND is_deleted = false)", nativeQuery = true)
	Boolean existsByEmailAndNotDeleted(@Param("email") String email);

	@Query(value = "SELECT c FROM candidate c WHERE c.isDraft = ?1 AND c.isDeleted = ?2 AND c.isActive = ?3")
	List<CandidateEntity> findAllByIsDraftAndIsDeletedAndIsActive(boolean isDraft, boolean isDeleted, boolean isActive);

}
