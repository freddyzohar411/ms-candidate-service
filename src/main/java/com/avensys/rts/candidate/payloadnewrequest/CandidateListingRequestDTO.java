package com.avensys.rts.candidate.payloadnewrequest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateListingRequestDTO {
	private Integer page = 0;
	private Integer pageSize = 5;
	private String sortBy;
	private String sortDirection;
	private String searchTerm;
	private List<String> searchFields;
	private Long jobId;
	private String customQuery;
	private Boolean allActive = false;
	private Boolean isDownload = false;
	private List<FilterDTO> filters;
}
