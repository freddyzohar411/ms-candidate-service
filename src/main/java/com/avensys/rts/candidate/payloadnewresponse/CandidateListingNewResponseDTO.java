package com.avensys.rts.candidate.payloadnewresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CandidateListingNewResponseDTO {
	private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;
    
    private List<CandidateNewListingDataDTO> candidates;

}
