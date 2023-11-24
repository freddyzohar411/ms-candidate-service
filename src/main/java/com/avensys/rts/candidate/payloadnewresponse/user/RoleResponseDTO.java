package com.avensys.rts.candidate.payloadnewresponse.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

	private Long id;
	private String roleName;
	private String roleDescription;
	private List<ModuleResponseDTO> modules;
}
