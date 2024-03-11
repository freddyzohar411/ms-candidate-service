package com.avensys.rts.candidate.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.candidate.entity.CandidateMappingEntity;
import com.avensys.rts.candidate.payloadnewrequest.CandidateMappingRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateMappingResponseDTO;
import com.avensys.rts.candidate.repository.CandidateMappingRepository;

@Service
public class CandidateMapping implements CandidateMappingService {

	@Autowired
	private CandidateMappingRepository candidateMappingRepository;

	private final String ENTITY_TYPE = "CANDIDATE";
	@Override
	public CandidateMappingResponseDTO saveCandidateMapping(CandidateMappingRequestDTO candidateListingRequestDTO) {
		Optional<CandidateMappingEntity> candidateMappingEntity = candidateMappingRepository.findByEntityType(ENTITY_TYPE);
		if(candidateMappingEntity.isPresent()){
			candidateMappingEntity.get().setCandidateMapping(candidateListingRequestDTO.getCandidateMapping());
			CandidateMappingEntity candidateSaved = candidateMappingRepository.save(candidateMappingEntity.get());
			return new CandidateMappingResponseDTO(candidateSaved.getCandidateMapping());
		}else{
			CandidateMappingEntity newCandidateMappingEntity = new CandidateMappingEntity();
			newCandidateMappingEntity.setEntityType(ENTITY_TYPE);
			newCandidateMappingEntity.setCandidateMapping(candidateListingRequestDTO.getCandidateMapping());
			CandidateMappingEntity candidateSaved = candidateMappingRepository.save(newCandidateMappingEntity);
			return new CandidateMappingResponseDTO(candidateSaved.getCandidateMapping());
		}

	}

	@Override
	public CandidateMappingResponseDTO getCandidateMapping() {
		Optional<CandidateMappingEntity> candidateMappingEntity = candidateMappingRepository.findByEntityType(ENTITY_TYPE);
		if (candidateMappingEntity.isPresent()) {
			return new CandidateMappingResponseDTO(candidateMappingEntity.get().getCandidateMapping());
		}
		return null;
	}
}
