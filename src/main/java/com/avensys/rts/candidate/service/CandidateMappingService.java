package com.avensys.rts.candidate.service;

import com.avensys.rts.candidate.payloadnewrequest.CandidateListingRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.CandidateMappingRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateMappingResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;

public interface CandidateMappingService {
	public CandidateMappingResponseDTO saveCandidateMapping(CandidateMappingRequestDTO candidateMappingRequestDTO);
	public CandidateMappingResponseDTO getCandidateMapping();
}
