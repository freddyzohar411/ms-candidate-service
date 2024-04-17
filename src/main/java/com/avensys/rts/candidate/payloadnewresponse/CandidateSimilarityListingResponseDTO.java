package com.avensys.rts.candidate.payloadnewresponse;

import java.util.List;

import com.avensys.rts.candidate.entity.CandidateEntityWithSimilarity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CandidateSimilarityListingResponseDTO {
	private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;
    
    private List<CandidateEntityWithSimilarity> candidates;
}
