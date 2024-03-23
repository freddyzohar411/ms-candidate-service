package com.avensys.rts.candidate.payloadnewrequest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateListingRequestDTO {
	private Integer page = 0;
    private Integer pageSize = 5;
    private String sortBy;
    private String sortDirection;
    private String searchTerm;
    private List<String> searchFields;
//    private Long jobId;
}
