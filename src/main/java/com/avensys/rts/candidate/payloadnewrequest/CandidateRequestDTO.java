package com.avensys.rts.candidate.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRequestDTO {
	private String firstName;
	private String lastName;
	private String email;

	// Form Submission
	private String formData;
	private Integer formId;

}
