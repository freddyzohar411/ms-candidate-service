package com.avensys.rts.candidate.payloadnewrequest;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmbeddingListCompareRequestDTO {
	private Set<String> jobAttributes;
	private Set<String> candidateAttributes;
}
