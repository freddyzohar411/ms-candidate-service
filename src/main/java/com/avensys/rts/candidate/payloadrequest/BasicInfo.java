package com.avensys.rts.candidate.payloadrequest;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicInfo {
	@NotEmpty(message = "firstName cannot be empty")
	private String firstName;
	@NotEmpty(message = "lastName cannot be empty")
	private String lastName;
	@NotEmpty(message = "gender cannot be empty")
	private String gender;
	@NotEmpty(message = "email cannot be empty")
	private String email;
	@NotEmpty(message = "phone cannot be empty")
	private String phone;
	@NotEmpty(message = "candidateNationality cannot be empty")
	private String candidateNationality;
	@NotEmpty(message = "currentLocation cannot be empty")
	private String currentLocation;
	@NotEmpty(message = "visaStatus cannot be empty")
	private String visaStatus;
	private String languageKnown;
	private String candidateOwner;

}
