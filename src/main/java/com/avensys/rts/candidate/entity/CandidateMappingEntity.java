package com.avensys.rts.candidate.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "candidate_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CandidateMappingEntity extends BaseEntity{

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	private String entityType;

	@JdbcTypeCode(SqlTypes.JSON)
	private JsonNode candidateMapping;
}
