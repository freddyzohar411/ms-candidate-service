package com.avensys.rts.candidate.payloadnewrequest;

import java.util.List;

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
	private String type;
	private List<String> columnName;
	private Integer createdBy;
	private Integer updatedBy;
}
