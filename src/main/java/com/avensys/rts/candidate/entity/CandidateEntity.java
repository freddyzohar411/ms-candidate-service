
package com.avensys.rts.candidate.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "candidate")
@Table(name = "candidate")
/**
 * @author Kotaiah nalleboina
 * This is the entity class for the cadidate table in the database
 */
public class CandidateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;
	
	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;
	
	@Column(name = "gender", length = 20, nullable = false)
	private String gender;
	
	@Column(name = "email", length = 250, nullable = false)
	private String email;
	
	@Column(name = "phone",length = 15, nullable = false)
	private String phone;
	
	@Column(name = "candidate_nationality", length = 50, nullable = false)
	private String candidateNationality;
	
	@Column(name = "current_location", length = 50, nullable = false)
	private String currentLocation;
	
	@Column(name = "visa_status", length = 20, nullable = false)
	private String visaStatus;
	
	@Column(name = "language_known", length = 250)
	private String languageKnown;
	
	@Column(name = "candidate_owner", length = 50)
	private String candidateOwner;
	
	@Column(name = " total_experience", nullable = false)
	private double totalExperience;
	
	@Column(name = "relevant_exprience",nullable = false)
	private double relevantExprience;
	
	@Column(name = "current_employer", length = 50, nullable = false)
	private String currentEmployer;
	
	@Column(name = "current_position_title", length = 50, nullable = false)
	private String currentPositionTitle;
	
	@Column(name = "candidate_current_salary", nullable = false)
	private double candidateCurrentSalary;
	
	@Column(name = "candidate_expected_salary", nullable = false)
	private double candidateExpectedSalary;
	
	@Column(name = "reason_for_change", length = 50, nullable = false)
	private String reasonForChange;
	
	@Column(name = "notice_period", nullable = false)
	private double noticePeriod;
	
	@Column(name = "profile_summary", length = 2500, nullable = false)
	private String profileSummary;
	
	@Column(name = "primary_skills", length = 500, nullable = false)
	private String primarySkills;
	
	@Column(name = "secondary_skills", length = 500)
	private String secondarySkills;
	
	@Column(name = "additional_info", length = 50)
	private String additionalInfo;
	
	@Column(name = "candidate_status", length = 50, nullable = false)
	private String candidateStatus;
	
	@Column(name = "source", length = 50, nullable = false)
	private String source;
	
	@Column(name = "referrers_name", length = 50)
	private String referrersName;
	
	@Column(name = "email_opt_out")
	private boolean emailOptOut;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column (name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
	

}
