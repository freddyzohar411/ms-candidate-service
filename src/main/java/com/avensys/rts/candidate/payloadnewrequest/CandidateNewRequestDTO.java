package com.avensys.rts.candidate.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateNewRequestDTO {
	private String firstName;
	private String lastName;
	private String gender;
	private String email;
	private String phone;
	private String candidateNationality;
	private String currentLocation;
	private String visaStatus;
	private String languageKnown;
	private String candidateOwner;
	private double totalExperience;
	private double relevantExprience;
	private String currentEmployer;
	private String currentPositionTitle;
	private double candidateCurrentSalary;
	private double candidateExpectedSalary;
	private String reasonForChange;
	private double noticePeriod;
	private String profileSummary;
	private String primarySkills;
	private String secondarySkills;
	private String additionalInfo;
	private String candidateStatus;
	private String source;
	private String referrersName;
	private boolean emailOptOut;
	
	// Form Submission
    private String formData;
    private Integer formId;

}
