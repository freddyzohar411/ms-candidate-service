package com.avensys.rts.candidate.payloadrequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfessionalInfo {
	 @NotNull(message = "totalExperience cannot be null")
	 private double totalExperience;
	 @NotNull(message = "relevantExperience cannot be null")
	 private double relevantExperience;
	 @NotEmpty(message = "currentEmployer cannot be empty")
	 private String currentEmployer;
	 @NotEmpty(message = "currentPositionTitle cannot be empty")
	 private String currentPositionTitle;
	 @NotNull(message = "candidateCurrentSalary cannot be null")
	 private double candidateCurrentSalary;
	 @NotNull(message = "candidateExpectedSalary cannot be null")
	 private double candidateExpectedSalary;
	 @NotEmpty(message = "reasonForChange cannot be empty")
	 private String reasonForChange;
	 @NotNull(message = "noticePeriod cannot be null")
	 private double noticePeriod;
	 @NotEmpty(message = "profileSummary cannot be empty")
	 private String profileSummary;
	 @NotEmpty(message = "primarySkills cannot be empty")
	 private String primarySkills;
	 private String secondarySkills;
	 private String additionalInfo;
	 @NotEmpty(message = "candidateStatus cannot be empty")
	 private String candidateStatus;
	 @NotEmpty(message = "source cannot be empty")
	 private String source;
	 private String referrersName;
	 private boolean emailOptOut;

}
