package com.avensys.rts.candidate.payloadnewresponse;

import java.time.LocalDateTime;

import com.avensys.rts.candidate.entity.CandidateNewEntity;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateNewListingDataDTO {
	private Integer id;
    private JsonNode candidateSubmissionData;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public CandidateNewListingDataDTO(CandidateNewEntity candidateNewEntity) {
    	this.id = candidateNewEntity.getId();
    	this.candidateSubmissionData = candidateNewEntity.getCandidateSubmissionData();
    	this.firstName = candidateNewEntity.getFirstName();
    	this.lastName = candidateNewEntity.getLastName();
    	this.createdAt = candidateNewEntity.getCreatedAt();
    	this.updatedAt = candidateNewEntity.getUpdatedAt();
    		
    }
    private String createdByName;
    private String updatedByName;

}
