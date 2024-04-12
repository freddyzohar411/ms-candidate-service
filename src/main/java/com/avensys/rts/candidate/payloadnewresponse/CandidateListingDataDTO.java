package com.avensys.rts.candidate.payloadnewresponse;

import java.time.LocalDateTime;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateListingDataDTO {
	private Integer id;
	private JsonNode candidateSubmissionData;
	private String firstName;
	private String lastName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public CandidateListingDataDTO(CandidateEntity candidateEntity) {
		this.id = candidateEntity.getId();
		this.candidateSubmissionData = candidateEntity.getCandidateSubmissionData();
		this.firstName = candidateEntity.getFirstName();
		this.lastName = candidateEntity.getLastName();
		this.createdAt = candidateEntity.getCreatedAt();
		this.updatedAt = candidateEntity.getUpdatedAt();

	}

	private String createdByName;
	private String updatedByName;

}
