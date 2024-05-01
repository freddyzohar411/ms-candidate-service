package com.avensys.rts.candidate.payloadnewrequest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateMatchingDetailsResponseDTO {
	private Long candidateId;
	private Long jobId;
	private Double qualificationScore;
	private Double normalizedQualificationScore;
	private JsonNode qualificationScoreDetails;
	private Double languageScore;
	private Double normalizedLanguageScore;
	private JsonNode languageScoreDetails;
	private Double skillsScore;
	private Double normalizedSkillsScore;
	private JsonNode skillsScoreDetails;
	private Double jobTitleScore;
	private Double normalizedJobTitleScore;
	private JsonNode jobTitleScoreDetails;
	private Double jobCountryScore;
	private Double normalizedJobCountryScore;
	private String jobCountryScoreDetails;
	private Double generalScore;
	private Double normalizedGeneralScore;
	private JsonNode generalScoreDetails;
	private Double fieldOfStudyScore;
	private Double normalizedFieldOfStudyScore;
	private JsonNode fieldOfStudyScoreDetails;
	private Double computedScore;
}
