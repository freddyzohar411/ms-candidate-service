package com.avensys.rts.candidate.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.fasterxml.jackson.databind.JsonNode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CandidateRepositoryTest {
	
	@Autowired
	CandidateRepository candidateRepository;

	CandidateEntity candidateEntity;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	JsonNode candidateSubmissionData;
	List<Long> createdByList;

	@BeforeEach
	void setUp() {
		candidateEntity = new CandidateEntity(1, "firstName","lastName",false,true,1,true,createdAt,1,updatedAt,1,1,candidateSubmissionData,"createdByUserGroupsId");
	}

	@AfterEach
	void tearDown() throws Exception {
		candidateRepository.deleteAll();
		candidateEntity = null;
	}

	@Test
	void testFindByEntityTypeAndEntityId() {
		Optional<CandidateEntity> candidateOptional = candidateRepository
				.findByIdAndDeleted(1,false,true);
		assertNotNull(candidateOptional);
	}
	
	@Test
	void testFindByUserAndDraftAndDeleted() {
		Optional<CandidateEntity> candidateOptional =  candidateRepository.findByUserAndDraftAndDeleted(1,false,false,true);
		assertNotNull(candidateOptional);

	}
	
	@Test
	void testFindAllByUserAndDraftAndDeleted() {
		List<CandidateEntity> candidateList = candidateRepository.findAllByUserAndDraftAndDeleted(1,false,false,true);
		assertNotNull(candidateList);
	}
	
	@Test
	void testFindAllByUserAndDeleted() {
		List<CandidateEntity> candidateList = candidateRepository.findAllByUserAndDeleted(1,false,true);
		assertNotNull(candidateList);
	}
	
	@Test
	void testFindAllByUserIdsAndDeleted() {
		List<CandidateEntity> candidateList = candidateRepository.findAllByUserIdsAndDeleted(createdByList,false ,true);
		assertNotNull(candidateList);
	}
	
	@Test
	void testFindByIdAndDraft() {
		Optional<CandidateEntity> candidateOptional = candidateRepository.findByIdAndDraft(1,false,true);
		assertNotNull(candidateOptional);
	}

}
