package com.avensys.rts.candidate.payloadnewresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CandidateListingResponseDTO {
	private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;
    
    private List<CandidateListingDataDTO> candidates;
}
