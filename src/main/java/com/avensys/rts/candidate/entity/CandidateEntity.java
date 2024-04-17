package com.avensys.rts.candidate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "candidate")
@Table(name = "candidate")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEntity {
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private int id;
	
	@Column(name = "firstName", length = 50, nullable = false)
	private String firstName;
	
	@Column(name = "lastName", length = 50, nullable = false)
	private String lastName;
	
	@Column (name = "is_deleted", columnDefinition = "boolean default false")
	private boolean isDeleted = false;
	
	@Column (name = "is_draft")
	private boolean isDraft = true;
	
	@Column (name = "created_by")
	private Integer createdBy;
	
	@Column (name = "is_active")
	private boolean isActive = true;
	
	@CreationTimestamp
    @Column (name = "created_at")
	private LocalDateTime createdAt;
	
	@Column (name = "updated_by")
	private Integer updatedBy;
	
	@UpdateTimestamp
    @Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@Column(name= "form_id")
	private Integer formId;
	
	@Column(name = "form_submission_id")
	private Integer formSubmissionId;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "candidate_submission_data")
	private JsonNode candidateSubmissionData;

	@Column(name = "created_by_user_groups_id", columnDefinition="TEXT")
	private String createdByUserGroupsId;

	@Column(name = "candidate_complete_info", columnDefinition="TEXT")
	private String candidateCompleteInfo;


}
