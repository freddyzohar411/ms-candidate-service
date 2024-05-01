package com.avensys.rts.candidate.payloadnewresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmbeddingListCompareResponseDTO {
	private Double normalized_score;
	private Double similarity_score;
	private List<SimilaritySetDTO> similar_attributes;
}
