package com.avensys.rts.candidate.payloadnewrequest;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmbeddingListTextCompareRequestDTO {
	private String jobAttributes;
	private Set<String> candidateAttributes;
}
