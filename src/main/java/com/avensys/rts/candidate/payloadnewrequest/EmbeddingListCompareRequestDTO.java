package com.avensys.rts.candidate.payloadnewrequest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmbeddingListCompareRequestDTO {
	private List<String> jobAttributes;
	private List<String> candidateAttributes;
}
