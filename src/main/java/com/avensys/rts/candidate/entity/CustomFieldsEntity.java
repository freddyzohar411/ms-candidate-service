package com.avensys.rts.candidate.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Author: Kotaiah Nalleboina
 * This is the entity class for the candidate custom view table in
 * the database.
 */
@Entity(name = "candidateCustomView")
@Table(name = "candidate_custom_view")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "column_name", length = 50000)
	private String columnName;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
