package com.avensys.rts.candidate.service;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

//import java.util.ArrayList;
//import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.candidate.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.candidate.APIClient.UserAPIClient;
import com.avensys.rts.candidate.entity.CandidateNewEntity;
import com.avensys.rts.candidate.payloadnewrequest.CandidateNewRequestDTO;
import com.avensys.rts.candidate.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateListingNewResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateNewListingDataDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateNewResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.candidate.payloadresponse.HttpResponse;
import com.avensys.rts.candidate.payloadresponse.UserResponseDTO;
import com.avensys.rts.candidate.repository.CandidateNewRepository;
import com.avensys.rts.candidate.util.JwtUtil;
import com.avensys.rts.candidate.util.MappingUtil;

import jakarta.transaction.Transactional;
@Service
public class CandidateNewServiceImpl implements CandidateNewService {

	private final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);
	@Autowired
	private CandidateNewRepository candidateNewRepository;
	@Autowired
	private UserAPIClient userAPIClient;
	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Override
	@Transactional
	public CandidateNewResponseDTO createCandidate(CandidateNewRequestDTO candidateNewRequestDTO) {
		LOG.info("Candidate create : Service");
		System.out.println("createCandidate"+candidateNewRequestDTO);
		CandidateNewEntity candidateNewEntity = candidateNewRequestDTOToCandidateNewEntity(candidateNewRequestDTO);
		System.out.println("Candidate ID: "+candidateNewEntity.getId());
		
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = candidateNewRequestDTOToFormSubmissionRequestDTO(candidateNewEntity,candidateNewRequestDTO);
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		
		candidateNewEntity.setCandidateSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
		candidateNewEntity.setFormSubmissionId(formSubmissionData.getId());
		System.out.println("Form Submission Id: " + candidateNewEntity.getFormSubmissionId());
		return candidateEntityToCandidateResopnseDTO(candidateNewEntity);
	}
	
	private CandidateNewResponseDTO candidateEntityToCandidateResopnseDTO(CandidateNewEntity candidateNewEntity) {
		CandidateNewResponseDTO candidateNewResponseDTO = new CandidateNewResponseDTO();
		candidateNewResponseDTO.setId(candidateNewEntity.getId());
		candidateNewResponseDTO.setFirstName(candidateNewEntity.getFirstName());
		candidateNewResponseDTO.setLastName(candidateNewEntity.getLastName());
		candidateNewResponseDTO.setFormId(candidateNewEntity.getFormId());
		candidateNewResponseDTO.setCreatedAt(candidateNewEntity.getCreatedAt());
		candidateNewResponseDTO.setUpdatedAt(candidateNewEntity.getUpdatedAt());
		
		// Get created by User data from user microservice
		HttpResponse userResponse = userAPIClient.getUserById(candidateNewEntity.getCreatedBy());
		UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),UserResponseDTO.class);
		candidateNewResponseDTO.setCreatedBy(userData.getFirstName()+ " " + userData.getLastName());
		
		// Get updated by user data from user microservice
		if(candidateNewEntity.getUpdatedBy()== candidateNewEntity.getCreatedBy()) {
			candidateNewResponseDTO.setUpdatedBy(userData.getFirstName()+ " " + userData.getLastName());
		}else {
			HttpResponse updatedByUserResponse = userAPIClient.getUserById(candidateNewEntity.getUpdatedBy());
			UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(),UserResponseDTO.class);
			candidateNewResponseDTO.setUpdatedBy(updatedByUserData.getFirstName()+ " " +updatedByUserData.getLastName());
		}
		// Get form submission data
		HttpResponse formSubmissionResponse = formSubmissionAPIClient .getFormSubmission(candidateNewEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(),FormSubmissionsResponseDTO.class);
		candidateNewResponseDTO.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));
		candidateNewResponseDTO.setCandidateSubmissionData(formSubmissionData.getSubmissionData());
		return candidateNewResponseDTO;
	}

	private CandidateNewEntity candidateNewRequestDTOToCandidateNewEntity(
			CandidateNewRequestDTO candidateNewRequestDTO) {
		System.out.println("candidateNewRequestDTOToCandidateNewEntity: "+candidateNewRequestDTO);
		CandidateNewEntity candidateNewEntity = new CandidateNewEntity();
		candidateNewEntity.setFirstName(candidateNewRequestDTO.getFirstName());
		candidateNewEntity.setLastName(candidateNewRequestDTO.getLastName());
		candidateNewEntity.setDraft(true);
		candidateNewEntity.setDeleted(false);
		candidateNewEntity.setUpdatedBy(getUserId());
		candidateNewEntity.setCreatedBy(getUserId());
		candidateNewEntity.setFormId(candidateNewRequestDTO.getFormId());
		return candidateNewRepository.save(candidateNewEntity);

	}

	private Integer getUserId() {
		String email = JwtUtil.getEmailFromContext();
		System.out.println("Hiiiiiiiiiiiiiiiiiii: "+email);
		HttpResponse userResponse = userAPIClient.getUserByEmail(email);
		UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
		return userData.getId();
	}

	private FormSubmissionsRequestDTO candidateNewRequestDTOToFormSubmissionRequestDTO(CandidateNewEntity candidateNewEntity,CandidateNewRequestDTO candidateNewRequestDTO ) {
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(getUserId());
		formSubmissionsRequestDTO.setFormId(candidateNewRequestDTO.getFormId());
		formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(candidateNewRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(candidateNewEntity.getId());
		formSubmissionsRequestDTO.setEntityType(null);
		return formSubmissionsRequestDTO;
	}

	@Override
	public CandidateNewResponseDTO getCandidate(Integer id) {
		//Get candidate data from candidate microservice
		CandidateNewEntity candidateNewEntity = candidateNewRepository.findByIdAndDeleted(id, false)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		return candidateEntityToCandidateResopnseDTO(candidateNewEntity);
	}

	@Override
	public CandidateNewResponseDTO updateCandidate(Integer id, CandidateNewRequestDTO candidateNewRequestDTO) {
		LOG.info("Candidate update : Service");
		System.out.println("Update candidate Id: " + id);
		System.out.println(candidateNewRequestDTO);
		
		// Get candidate data from candidate microservice
		CandidateNewEntity candidateNewEntity = candidateNewRepository.findByIdAndDeleted(id, false)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		// Update candidate data
		candidateNewEntity.setFirstName(candidateNewRequestDTO.getFirstName());
		candidateNewEntity.setLastName(candidateNewRequestDTO.getLastName());
		candidateNewEntity.setUpdatedBy(getUserId());
		candidateNewEntity.setFormId(candidateNewRequestDTO.getFormId());
		candidateNewRepository.save(candidateNewEntity);
		
		// Update form submission data
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = candidateNewRequestDTOToFormSubmissionRequestDTO(candidateNewEntity,candidateNewRequestDTO);
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.updateFormSubmission(candidateNewEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		
		// Added - Update candidateSubmissionData
		candidateNewEntity.setCandidateSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
		return candidateEntityToCandidateResopnseDTO(candidateNewEntity);
	}
	
	// Delete Draft candidate
	@Override
	public void deleteDraftCandidate(Integer id) {
		// Get candidate which is in draft state.
		CandidateNewEntity candidateNewEntity = candidateNewRepository.findByIdAndDraft(id, true).orElseThrow(() -> new RuntimeException("Candidate not found"));
		
		 // Delete all candidate form submission
		if(candidateNewEntity.getFormSubmissionId() != null) {
			HttpResponse formSubmissionResponse = formSubmissionAPIClient.deleteFormSubmission(candidateNewEntity.getFormSubmissionId());
		}
	}
	// 
	@Override
	public CandidateNewResponseDTO getCandidateIfDraft() {
		Optional<CandidateNewEntity> candidateNewEntity = candidateNewRepository.findByUserAndDraftAndDeleted(getUserId(),true,false);
		if(candidateNewEntity.isPresent()) {
			return candidateEntityToCandidateResopnseDTO(candidateNewEntity.get());
		}
		return null;
	}
	
	// Soft delete operation for candidate
	@Override
	public void softDeleteCandidate(Integer id) {
		CandidateNewEntity candidateNewEntity = candidateNewRepository.findByIdAndDeleted(id,false).orElseThrow(() -> new RuntimeException("Candidate not found"));
		// Soft delete the candidate
		candidateNewEntity.setDeleted(true);
		
		// Save candidate
		candidateNewRepository.save(candidateNewEntity);
	}
	
	// Get all candidates by user
	@Override
	public List<CandidateNewEntity> getAllCandidatesByUser(boolean draft, boolean deleted) {
		List<CandidateNewEntity> CandidateEntities = candidateNewRepository.findAllByUserAndDraftAndDeleted(getUserId(), draft, deleted);
		return CandidateEntities;
	}
	
	// Candidate Listing page with search
	@Override
	public CandidateListingNewResponseDTO getCandidateListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
		Page<CandidateNewEntity> candidateEntitiesPage = null;
		try {
			candidateEntitiesPage = candidateNewRepository.findAllByOrderByAndSearchNumeric(getUserId(), false,false,true, pageRequest, searchFields, searchTerm);
			
		}catch(Exception e){
			candidateEntitiesPage = candidateNewRepository.findAllByOrderByAndSearchString(getUserId(), false,false,true, pageRequest, searchFields, searchTerm);
		}
		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
	}
	
	private CandidateListingNewResponseDTO pageCandidateListingToCandidateListingResponseDTO(Page<CandidateNewEntity> candidateNewEntitiesPage) {
		CandidateListingNewResponseDTO candidateListingNewResponseDTO = new CandidateListingNewResponseDTO();
		candidateListingNewResponseDTO.setTotalPages(candidateNewEntitiesPage.getTotalPages());
		candidateListingNewResponseDTO.setTotalElements(candidateNewEntitiesPage.getTotalElements());
		candidateListingNewResponseDTO.setPage(candidateNewEntitiesPage.getNumber());
		candidateListingNewResponseDTO.setPageSize(candidateNewEntitiesPage.getSize());
		List<CandidateNewListingDataDTO> candidateNewListingDataDTOs = new ArrayList<>();
		candidateNewListingDataDTOs = candidateNewEntitiesPage.getContent().stream().map(
				candidateNewEntity ->{
					CandidateNewListingDataDTO candidateNewListingDataDTO = new CandidateNewListingDataDTO(candidateNewEntity);
					
					// Get created by User data from user service
					HttpResponse userResponse = userAPIClient.getUserById(candidateNewEntity.getCreatedBy());
					UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),UserResponseDTO.class);
					candidateNewListingDataDTO.setCreatedByName(userData.getFirstName()+ " " + userData.getLastName());
					
					// Get updated by User data from user service
					HttpResponse updatedByUserResponse = userAPIClient.getUserById(candidateNewEntity.getUpdatedBy());
					UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(),UserResponseDTO.class);
					candidateNewListingDataDTO.setUpdatedByName(updatedByUserData.getFirstName()+ " " +updatedByUserData.getLastName());
					return candidateNewListingDataDTO;
				}
				).toList();
		candidateListingNewResponseDTO.setCandidates(candidateNewListingDataDTOs);
		return candidateListingNewResponseDTO;
	}

	@Override
	public CandidateListingNewResponseDTO getCandidateListingPage(Integer page, Integer size, String sortBy,
			String sortDirection) {
		// Get sort direction
		 Sort.Direction direction = Sort.DEFAULT_DIRECTION;
	        if (sortDirection != null && !sortDirection.isEmpty()) {
	            direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
	        }
	        if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
	            sortBy = "updated_at";
	            direction = Sort.Direction.DESC;
	        }
	        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
	        Page<CandidateNewEntity> candidateEntitiesPage = null;
	     // Try with numeric first else try with string (jsonb)
	        try {
	        	candidateEntitiesPage = candidateNewRepository.findAllByOrderByNumeric( getUserId(), false, false, true, pageRequest);
	        }catch (Exception e) {
	        	candidateEntitiesPage = candidateNewRepository.findAllByOrderByString(getUserId(), false, false, true, pageRequest);
	        }
		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
	}

//	@Override
//	public List<CandidateNewEntity> getAllCandidatesWithSearch(String query) {
//		List<CandidateNewEntity>candidateEntities = candidateNewRepository.getAllCandidatesWithSearch(query, getUserId(), false, false);
//		return candidateEntities;
//	}

}
