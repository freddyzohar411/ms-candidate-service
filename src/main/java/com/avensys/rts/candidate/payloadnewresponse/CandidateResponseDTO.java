package com.avensys.rts.candidate.payloadnewresponse;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponseDTO {
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

	/**
	 * @author Kotaiah nalleboina This class is used to create a custom response for
	 *         the API calls. It is used to return a custom response to the client.
	 */

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class HttpResponse {
		private int code;
		private boolean error;
		private String message;
		private Object data;
		private Map<?, ?> audit;
		private LocalDateTime timestamp = LocalDateTime.now();
	}

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UserResponseDTO {
		private Integer id;
		private String firstName;
		private String lastName;
		private String username;
		private String email;
		private String mobile;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private Boolean locked;
		private Boolean enabled;

	}
}
