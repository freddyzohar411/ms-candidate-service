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
	private JsonNode qualificationScoreDetails;
	private Double languageScore;
	private JsonNode languageScoreDetails;
	private Double skillsScore;
	private JsonNode skillsScoreDetails;
	private Double jobTitleScore;
	private JsonNode jobTitleScoreDetails;
	private Double jobCountryScore;
	private String jobCountryScoreDetails;
	private Double generalScore;
	private JsonNode generalScoreDetails;
}
