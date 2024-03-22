package com.avensys.rts.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.candidate.entity.CustomFieldsEntity;

public interface CandidateCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long>  {

}
