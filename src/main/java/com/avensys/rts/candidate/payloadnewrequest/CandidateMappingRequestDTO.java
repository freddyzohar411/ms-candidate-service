package com.avensys.rts.candidate.payloadnewrequest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateMappingRequestDTO {
	JsonNode candidateMapping;
}
