package com.avensys.rts.candidate.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.payloadrequest.CandidateRequest;
import com.avensys.rts.candidate.repository.CandidateRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class CandidateServiceImpl implements CandidateService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);
	@Autowired
	private CandidateRepository candidateRepository;
	/**
	 * This method is used to save Candidate Data
	 */
	@Override
	public CandidateEntity saveCandidateData(CandidateRequest candidateRequest) {
		LOG.info("saveCandidateData request processing");
		CandidateEntity candidateEntity = mapRequestToEntity(candidateRequest, null);
		return candidateRepository.save(candidateEntity);
	}
	/**This method is used to retrieve a  candidate Information
	 *
	 */
	@Override
	public CandidateEntity getCandidateData(Integer id) {
		LOG.info("getCandidateData request processing");
		CandidateEntity candidateEntity = candidateRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                () -> new EntityNotFoundException("candidate with %s not found".formatted(id))
        );
		LOG.info("Candidate retrieved : Service");
		return candidateEntity;
	}
	/**
	 * This method is used to update candidate data
	 */
	@Override
	public CandidateEntity updateCandidateData(Integer id, CandidateRequest candidateRequest) {
		LOG.info("updateCandidateData request processing");
		CandidateEntity candidateEntity = candidateRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                () -> new EntityNotFoundException("Candidate with %s not found".formatted(id))
        );
		candidateEntity = mapRequestToEntity(candidateRequest, candidateEntity);
		LOG.info("Candidate updated : Service");
		return candidateRepository.save(candidateEntity);
	}
	/**
	 * This method is used to delete candidate data
	 */
	@Override
	public void deleteCandidateData(Integer id) {
		CandidateEntity candidateEntity = candidateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Candidate with %s not found".formatted(id))
        );
		candidateEntity.setDeleted(true);
		candidateRepository.save(candidateEntity);
		LOG.info("Job deleted : Service");
	}
	
	/**
	 * This method is used to convert CandidateRequest to CandidateEntity
	 * @param candidateRequest
	 * @param candidateEntity
	 * @return
	 */
	private CandidateEntity mapRequestToEntity(CandidateRequest candidateRequest, CandidateEntity candidateEntity) {
		if(candidateEntity == null)
			candidateEntity = new CandidateEntity();

		if(ObjectUtils.isNotEmpty(candidateRequest.getBasicInfo())) {
			candidateEntity.setFirstName(candidateRequest.getBasicInfo().getFirstName());
			candidateEntity.setLastName(candidateRequest.getBasicInfo().getLastName());
			candidateEntity.setGender(candidateRequest.getBasicInfo().getGender());
			candidateEntity.setEmail(candidateRequest.getBasicInfo().getEmail());
			candidateEntity.setPhone(candidateRequest.getBasicInfo().getPhone());
			candidateEntity.setCandidateNationality(candidateRequest.getBasicInfo().getCandidateNationality());
			candidateEntity.setCurrentLocation(candidateRequest.getBasicInfo().getCurrentLocation());
			candidateEntity.setVisaStatus(candidateRequest.getBasicInfo().getVisaStatus());
			candidateEntity.setLanguageKnown(candidateRequest.getBasicInfo().getLanguageKnown());
			candidateEntity.setCandidateOwner(candidateRequest.getBasicInfo().getCandidateOwner());
		}
		
		if(ObjectUtils.isNotEmpty(candidateRequest.getProfessionalInfo())) {
		    candidateEntity.setTotalExperience(candidateRequest.getProfessionalInfo().getTotalExperience());
		    candidateEntity.setRelevantExprience(candidateRequest.getProfessionalInfo().getRelevantExperience());
		    candidateEntity.setCurrentEmployer(candidateRequest.getProfessionalInfo().getCurrentEmployer());
		    candidateEntity.setCurrentPositionTitle(candidateRequest.getProfessionalInfo().getCurrentPositionTitle());
		    candidateEntity.setCandidateCurrentSalary(candidateRequest.getProfessionalInfo().getCandidateCurrentSalary());
		    candidateEntity.setCandidateExpectedSalary(candidateRequest.getProfessionalInfo().getCandidateExpectedSalary());
		    candidateEntity.setReasonForChange(candidateRequest.getProfessionalInfo().getReasonForChange());
		    candidateEntity.setNoticePeriod(candidateRequest.getProfessionalInfo().getNoticePeriod());
		    candidateEntity.setProfileSummary(candidateRequest.getProfessionalInfo().getProfileSummary());
		    candidateEntity.setPrimarySkills(candidateRequest.getProfessionalInfo().getPrimarySkills());
		    candidateEntity.setSecondarySkills(candidateRequest.getProfessionalInfo().getSecondarySkills());
		    candidateEntity.setAdditionalInfo(candidateRequest.getProfessionalInfo().getAdditionalInfo());
		    candidateEntity.setCandidateStatus(candidateRequest.getProfessionalInfo().getCandidateStatus());
		    candidateEntity.setSource(candidateRequest.getProfessionalInfo().getSource());
		    candidateEntity.setReferrersName(candidateRequest.getProfessionalInfo().getReferrersName());
		    
		    
		    
		}
		
		return candidateEntity;
	}
	@Override
	public List<CandidateEntity> getAllCandidateData() {
		LOG.info("getAllCandidateData request processing");
		List<CandidateEntity> candidateEntityList = candidateRepository.findAllAndIsDeleted(false);
		LOG.info("candidateEntityList retrieved : Service");
		return candidateEntityList;
	}
	
}
