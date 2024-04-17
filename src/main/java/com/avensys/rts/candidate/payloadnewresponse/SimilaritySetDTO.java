package com.avensys.rts.candidate.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SimilaritySetDTO {
	private String job_attribute;
	private String candidate_attribute;
	private Double score;
}
