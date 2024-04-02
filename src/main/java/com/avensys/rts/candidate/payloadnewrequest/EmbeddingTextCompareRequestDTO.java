package com.avensys.rts.candidate.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmbeddingTextCompareRequestDTO {
	private String jobAttributes;
	private String candidateAttributes;
	private String modelName;
}
