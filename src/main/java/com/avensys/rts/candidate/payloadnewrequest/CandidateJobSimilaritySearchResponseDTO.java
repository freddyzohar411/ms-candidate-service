package com.avensys.rts.candidate.payloadnewrequest;

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
}
