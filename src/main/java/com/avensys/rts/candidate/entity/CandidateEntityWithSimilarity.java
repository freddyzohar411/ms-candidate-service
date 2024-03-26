package com.avensys.rts.candidate.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEntityWithSimilarity extends CandidateEntity {
	private Double similarityScore;
	private Double qualificationScore;
	private JsonNode qualificationScoreDetails;
	private Double languageScore;
	private JsonNode languageScoreDetails;
	private Double skillsScore;
	private JsonNode skillsScoreDetails;
	private Double jobTitleScore;
	private JsonNode jobTitleScoreDetails;
	private Double jobCountryScore;
	private String jobCountryScoreDetails;
	private Double computedScore;

	public CandidateEntityWithSimilarity(CandidateEntity candidateEntity, Double similarityScore) {
		this.setId(candidateEntity.getId());
		this.setFirstName(candidateEntity.getFirstName());
		this.setLastName(candidateEntity.getLastName());
		this.setCreatedBy(candidateEntity.getCreatedBy());
		this.setCreatedAt(candidateEntity.getCreatedAt());
		this.setUpdatedBy(candidateEntity.getUpdatedBy());
		this.setUpdatedAt(candidateEntity.getUpdatedAt());
		this.setFormId(candidateEntity.getFormId());
		this.setFormSubmissionId(candidateEntity.getFormSubmissionId());
		this.setCandidateSubmissionData(candidateEntity.getCandidateSubmissionData());
		this.setCreatedByUserGroupsId(candidateEntity.getCreatedByUserGroupsId());
		this.similarityScore = similarityScore;
	}
}
