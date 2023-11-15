package com.avensys.rts.candidate.payloadnewresponse;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateNewResponseDTO {
	private Integer id;
    private String firstName;
    private String lastName;
    private Integer formId;
    private String submissionData;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private JsonNode candidateSubmissionData;

}
