package com.avensys.rts.candidate.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldsRequestDTO {
	private Long id;
	private String name;
	private String columnName;
	private Integer createdBy;
	private Integer updatedBy;
}
