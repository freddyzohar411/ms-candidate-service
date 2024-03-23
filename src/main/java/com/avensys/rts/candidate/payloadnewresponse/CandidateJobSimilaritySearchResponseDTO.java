package com.avensys.rts.candidate.payloadnewresponse;

import com.avensys.rts.candidate.entity.CandidateEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateJobSimilaritySearchResponseDTO {
	private CandidateEntity candidate;
	private Double similarityScore;
	private Double similaritySum;
	private Double basicInfoSimilarity;
	private Double educationSimilarity;
	private Double workExperienceSimilarity;
}
