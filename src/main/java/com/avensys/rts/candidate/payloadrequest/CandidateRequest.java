package com.avensys.rts.candidate.payloadrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRequest {
	private ProfessionalInfo professionalInfo;
	private BasicInfo basicInfo;
}
