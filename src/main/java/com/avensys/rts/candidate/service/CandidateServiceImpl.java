package com.avensys.rts.candidate.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

//import java.util.ArrayList;
//import java.util.List;

import com.avensys.rts.candidate.APIClient.*;
import com.avensys.rts.candidate.entity.CandidateEntityWithSimilarity;
import com.avensys.rts.candidate.model.FieldInformation;
import com.avensys.rts.candidate.payloadnewrequest.*;
import com.avensys.rts.candidate.payloadnewresponse.*;
import com.avensys.rts.candidate.util.StringUtil;
import com.avensys.rts.candidate.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.repository.CandidateRepository;
import com.avensys.rts.candidate.util.JwtUtil;
import com.avensys.rts.candidate.util.MappingUtil;

import jakarta.transaction.Transactional;

@Service
public class CandidateServiceImpl implements CandidateService {

	private final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);

	private final String CANDIDATE_BASIC_INFO_ENTITY_TYPE = "candidate_basic_info";

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private EducationDetailsAPIClient educationDetailsAPIClient;
	@Autowired
	private EmployerDetailsAPIClient employerDetailsAPIClient;
	@Autowired
	private LanguagesAPIClient languagesAPIClient;
	@Autowired
	private CertificationAPIClient certificationAPIClient;
	@Autowired
	private WorkExperienceAPIClient workExperienceAPIClient;
	@Autowired
	private DocumentAPIClient documentAPIClient;
	@Autowired
	private CandidateRepository candidateRepository;
	@Autowired
	private UserAPIClient userAPIClient;
	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;
	@Autowired
	private EmbeddingAPIClient embeddingAPIClient;

	@Override
	@Transactional
	public CandidateResponseDTO createCandidate(CandidateRequestDTO candidateRequestDTO) {
		LOG.info("Candidate create : Service");
		System.out.println("createCandidate" + candidateRequestDTO);
		CandidateEntity candidateEntity = candidateNewRequestDTOToCandidateNewEntity(candidateRequestDTO);
		System.out.println("Candidate ID: " + candidateEntity.getId());

		FormSubmissionsRequestDTO formSubmissionsRequestDTO = candidateNewRequestDTOToFormSubmissionRequestDTO(
				candidateEntity, candidateRequestDTO);
		CandidateResponseDTO.HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		candidateEntity.setCandidateSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
		candidateEntity.setFormSubmissionId(formSubmissionData.getId());
		System.out.println("Form Submission Id: " + candidateEntity.getFormSubmissionId());
		return candidateEntityToCandidateResopnseDTO(candidateEntity);
	}

	private CandidateResponseDTO candidateEntityToCandidateResopnseDTO(CandidateEntity candidateEntity) {
		CandidateResponseDTO candidateResponseDTO = new CandidateResponseDTO();
		candidateResponseDTO.setId(candidateEntity.getId());
		candidateResponseDTO.setFirstName(candidateEntity.getFirstName());
		candidateResponseDTO.setLastName(candidateEntity.getLastName());
		candidateResponseDTO.setFormId(candidateEntity.getFormId());
		candidateResponseDTO.setCreatedAt(candidateEntity.getCreatedAt());
		candidateResponseDTO.setUpdatedAt(candidateEntity.getUpdatedAt());

		// Get created by User data from user microservice
		CandidateResponseDTO.HttpResponse userResponse = userAPIClient.getUserById(candidateEntity.getCreatedBy());
		CandidateResponseDTO.UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				CandidateResponseDTO.UserResponseDTO.class);
		candidateResponseDTO.setCreatedBy(userData.getFirstName() + " " + userData.getLastName());

		// Get updated by user data from user microservice
		if (candidateEntity.getUpdatedBy() == candidateEntity.getCreatedBy()) {
			candidateResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		} else {
			CandidateResponseDTO.HttpResponse updatedByUserResponse = userAPIClient
					.getUserById(candidateEntity.getUpdatedBy());
			CandidateResponseDTO.UserResponseDTO updatedByUserData = MappingUtil
					.mapClientBodyToClass(updatedByUserResponse.getData(), CandidateResponseDTO.UserResponseDTO.class);
			candidateResponseDTO.setUpdatedBy(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
		}
		// Get form submission data
		CandidateResponseDTO.HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.getFormSubmission(candidateEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		candidateResponseDTO
				.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));
		candidateResponseDTO.setCandidateSubmissionData(formSubmissionData.getSubmissionData());
		return candidateResponseDTO;
	}

	private CandidateEntity candidateNewRequestDTOToCandidateNewEntity(CandidateRequestDTO candidateRequestDTO) {
		System.out.println("candidateNewRequestDTOToCandidateNewEntity: " + candidateRequestDTO);
		CandidateEntity candidateEntity = new CandidateEntity();
		candidateEntity.setFirstName(candidateRequestDTO.getFirstName());
		candidateEntity.setLastName(candidateRequestDTO.getLastName());
		candidateEntity.setDraft(true);
		candidateEntity.setDeleted(false);
		candidateEntity.setUpdatedBy(getUserId());
		candidateEntity.setCreatedBy(getUserId());
		candidateEntity.setFormId(candidateRequestDTO.getFormId());
		return candidateRepository.save(candidateEntity);

	}

	private Integer getUserId() {
		String email = JwtUtil.getEmailFromContext();
		CandidateResponseDTO.HttpResponse userResponse = userAPIClient.getUserByEmail(email);
		CandidateResponseDTO.UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				CandidateResponseDTO.UserResponseDTO.class);
		return userData.getId();
	}

	private FormSubmissionsRequestDTO candidateNewRequestDTOToFormSubmissionRequestDTO(CandidateEntity candidateEntity,
			CandidateRequestDTO candidateRequestDTO) {
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(getUserId());
		formSubmissionsRequestDTO.setFormId(candidateRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(candidateRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(candidateEntity.getId());
		formSubmissionsRequestDTO.setEntityType(CANDIDATE_BASIC_INFO_ENTITY_TYPE);
		return formSubmissionsRequestDTO;
	}

	@Override
	public CandidateResponseDTO getCandidate(Integer id) {
		// Get candidate data from candidate microservice
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		return candidateEntityToCandidateResopnseDTO(candidateEntity);
	}

	@Override
	public CandidateResponseDTO updateCandidate(Integer id, CandidateRequestDTO candidateRequestDTO) {
		LOG.info("Candidate update : Service");
		System.out.println("Update candidate Id: " + id);
		System.out.println(candidateRequestDTO);

		// Get candidate data from candidate microservice
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		// Update candidate data
		candidateEntity.setFirstName(candidateRequestDTO.getFirstName());
		candidateEntity.setLastName(candidateRequestDTO.getLastName());
		candidateEntity.setUpdatedBy(getUserId());
		candidateEntity.setFormId(candidateRequestDTO.getFormId());
		candidateRepository.save(candidateEntity);

		// Update form submission data
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = candidateNewRequestDTOToFormSubmissionRequestDTO(
				candidateEntity, candidateRequestDTO);
		CandidateResponseDTO.HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.updateFormSubmission(candidateEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		// Added - Update candidateSubmissionData
		candidateEntity.setCandidateSubmissionData(formSubmissionData.getSubmissionData());
		candidateRepository.save(candidateEntity);
		return candidateEntityToCandidateResopnseDTO(candidateEntity);
	}

	@Override
	public Set<FieldInformation> getAllCandidatesFields() {
		List<CandidateEntity> candidateEntities = candidateRepository
				.findAllByUserIdsAndDeleted(userUtil.getUsersIdUnderManager(), false, true);
		if (candidateEntities.isEmpty()) {
			return null;
		}

		// Declare a new haspmap to store the label and value
		Set<FieldInformation> fieldColumn = new HashSet<>();
		fieldColumn.add(new FieldInformation("Created At", "createdAt", true, "created_at"));
		fieldColumn.add(new FieldInformation("Updated At", "updatedAt", true, "updated_at"));
		fieldColumn.add(new FieldInformation("Created By", "createdByName", false, null));

		// Loop through the account submission data jsonNode
		for (CandidateEntity candidateEntity : candidateEntities) {
			if (candidateEntity.getCandidateSubmissionData() != null) {
				Iterator<String> accountFieldNames = candidateEntity.getCandidateSubmissionData().fieldNames();
				while (accountFieldNames.hasNext()) {
					String fieldName = accountFieldNames.next();
					fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"candidateSubmissionData." + fieldName, true, "candidate_submission_data." + fieldName));
				}
			}
		}
		return fieldColumn;
	}

	// Delete Draft candidate (Incomplete)
	@Override
	@Transactional
	public void deleteDraftCandidate(Integer id) {
		// Get candidate which is in draft state.
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDraft(id, true, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		// Delete all the document service related to candidate
		CandidateResponseDTO.HttpResponse documentResponse = documentAPIClient
				.deleteDocumentsByEntityTypeAndEntityId("candidate_documents", id);
		// Delete all the work experience
		CandidateResponseDTO.HttpResponse workExperienceResponse = workExperienceAPIClient
				.deleteWorkExperienceByEntityTypeAndEntityId("candidate_work_experience", id);
		// Delete all the education
		CandidateResponseDTO.HttpResponse educationResponse = educationDetailsAPIClient
				.deleteEducationDetailsByEntityTypeAndEntityId("candidate_education_details", id);
		// Delete all the certification
		CandidateResponseDTO.HttpResponse certificationResponse = certificationAPIClient
				.deleteCertificationsByEntityTypeAndEntityId("candidate_certification", id);
		// Delete all the languages
		CandidateResponseDTO.HttpResponse languagesResponse = languagesAPIClient
				.deleteLanguagesByEntityTypeAndEntityId("candidate_languages", id);
		// Delete all the employer details
		CandidateResponseDTO.HttpResponse employerDetailsResponse = employerDetailsAPIClient
				.deleteEmployerDetailsByEntityTypeAndEntityId("candidate_employer_details", id);

		// Delete all candidate form submission
		if (candidateEntity.getFormSubmissionId() != null) {
			CandidateResponseDTO.HttpResponse formSubmissionResponse = formSubmissionAPIClient
					.deleteFormSubmission(candidateEntity.getFormSubmissionId());
		}

		// Delete the entire candidate
		candidateRepository.delete(candidateEntity);

	}

	// Get candidate if draft
	@Override
	public CandidateResponseDTO getCandidateIfDraft() {
		Optional<CandidateEntity> candidateNewEntity = candidateRepository.findByUserAndDraftAndDeleted(getUserId(),
				true, false, true);
		if (candidateNewEntity.isPresent()) {
			return candidateEntityToCandidateResopnseDTO(candidateNewEntity.get());
		}
		return null;
	}

	// Soft delete operation for candidate
	@Override
	public void softDeleteCandidate(Integer id) {
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		// Soft delete the candidate
		candidateEntity.setDeleted(true);

		// Save candidate
		candidateRepository.save(candidateEntity);
	}

	// Get all candidates by user
	@Override
	public List<CandidateEntity> getAllCandidatesByUser(boolean draft, boolean deleted) {
		List<CandidateEntity> CandidateEntities = candidateRepository.findAllByUserAndDraftAndDeleted(getUserId(),
				draft, deleted, true);
		return CandidateEntities;
	}

	// Complete candidate creation (Set draft to false)
	@Override
	public CandidateResponseDTO completeCandidateCreate(Integer id) {
		// Get candidate data from candidate microservice
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		// Update candidate data
		candidateEntity.setDraft(false);
		candidateEntity.setUpdatedBy(getUserId());
		candidateEntity.setCreatedByUserGroupsId(userUtil.getUserGroupIdsAsString());
		candidateRepository.save(candidateEntity);
		return candidateEntityToCandidateResopnseDTO(candidateEntity);
	}

	/**
	 * Get candidate data, only basic info
	 * 
	 * @param candidateId
	 * @return
	 */
	@Override
	public CandidateListingDataDTO getCandidateByIdData(Integer candidateId) {
		return candidateEntityToCandidateNewListingDataDTO(
				candidateRepository.findByIdAndDeleted(candidateId, false, true)
						.orElseThrow(() -> new RuntimeException("Candidate not found")));
	}

	/**
	 * Get all candidate data including all related microservices
	 * 
	 * @param candidateId
	 * @return
	 */
	@Override
	public HashMap<String, Object> getCandidateByIdDataAll(Integer candidateId) {
		HashMap<String, Object> candidateData = new HashMap<>();
		// Get basic information from form submission
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(candidateId, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		candidateData.put("basicInfo", candidateEntity.getCandidateSubmissionData());

		// Get work experience from work experience microservice
		CandidateResponseDTO.HttpResponse workExperienceResponse = workExperienceAPIClient
				.getWorkExperienceByEntityTypeAndEntityId("candidate_work_experience", candidateId);

		// Get the form submission data from work experience microservice
		List<Object> workExperienceSubmissionData = MappingUtil.mapClientBodyToClass(workExperienceResponse.getData(),
				List.class);
		List<JsonNode> workExperienceSubmissionDataJsonNodeList = MappingUtil
				.convertObjectToListOfJsonNode(workExperienceSubmissionData, "submissionData");
		candidateData.put("workExperiences", workExperienceSubmissionDataJsonNodeList);

		// Get education details from education details microservice
		List<Object> educationDetailsSubmissionData = MappingUtil.mapClientBodyToClass(educationDetailsAPIClient
				.getEducationDetailsByEntityTypeAndEntityId("candidate_education_details", candidateId).getData(),
				List.class);
		List<JsonNode> educationDetailsSubmissionDataJsonNodeList = MappingUtil
				.convertObjectToListOfJsonNode(educationDetailsSubmissionData, "submissionData");
		candidateData.put("educationDetails", educationDetailsSubmissionDataJsonNodeList);

		// Get certification from certification microservice
		List<Object> certificationSubmissionData = MappingUtil.mapClientBodyToClass(certificationAPIClient
				.getCertificationsByEntityTypeAndEntityId("candidate_certification", candidateId).getData(),
				List.class);
		List<JsonNode> certificationSubmissionDataJsonNodeList = MappingUtil
				.convertObjectToListOfJsonNode(certificationSubmissionData, "submissionData");
		candidateData.put("certification", certificationSubmissionDataJsonNodeList);

		// Get languages from languages microservice
		List<Object> languagesSubmissionData = MappingUtil.mapClientBodyToClass(
				languagesAPIClient.getLanguagesByEntityTypeAndEntityId("candidate_languages", candidateId).getData(),
				List.class);
		List<JsonNode> languagesSubmissionDataJsonNodeList = MappingUtil
				.convertObjectToListOfJsonNode(languagesSubmissionData, "submissionData");
		candidateData.put("languages", languagesSubmissionDataJsonNodeList);

		// Get employer details from employer details microservice
		List<Object> employerDetailsSubmissionData = MappingUtil.mapClientBodyToClass(
				employerDetailsAPIClient
						.getEmployerDetailsByEntityTypeAndEntityId("candidate_employer_details", candidateId).getData(),
				List.class);
		List<JsonNode> employerDetailsSubmissionDataJsonNodeList = MappingUtil
				.convertObjectToListOfJsonNode(employerDetailsSubmissionData, "submissionData");
		candidateData.put("employerDetails", employerDetailsSubmissionDataJsonNodeList);

		return candidateData;
	}

	/**
	 * Get all the fields for all the forms in the candidate service including all
	 * related microservices
	 * 
	 * @return
	 */
	@Override
	public HashMap<String, List<HashMap<String, String>>> getAllCandidatesFieldsAll() {
		HashMap<String, List<HashMap<String, String>>> allFields = new HashMap<>();

		// Get Basic Info Fields
		CandidateResponseDTO.HttpResponse candidateBasicInfo = formSubmissionAPIClient
				.getFormFieldNameList("candidate_basic_info");
		List<HashMap<String, String>> candidateBasicInfoFields = MappingUtil
				.mapClientBodyToClass(candidateBasicInfo.getData(), List.class);
		allFields.put("basicInfo", candidateBasicInfoFields);

		// Get Work Experience Fields
		CandidateResponseDTO.HttpResponse workExperience = formSubmissionAPIClient
				.getFormFieldNameList("candidate_work_experience");
		List<HashMap<String, String>> workExperienceFields = MappingUtil.mapClientBodyToClass(workExperience.getData(),
				List.class);
		allFields.put("workExperiences", workExperienceFields);

		// Get Education Details Fields
		CandidateResponseDTO.HttpResponse educationDetails = formSubmissionAPIClient
				.getFormFieldNameList("candidate_education_details");
		List<HashMap<String, String>> educationDetailsFields = MappingUtil
				.mapClientBodyToClass(educationDetails.getData(), List.class);
		allFields.put("educationDetails", educationDetailsFields);

		// Get Certification Fields
		CandidateResponseDTO.HttpResponse certification = formSubmissionAPIClient
				.getFormFieldNameList("candidate_certification");
		List<HashMap<String, String>> certificationFields = MappingUtil.mapClientBodyToClass(certification.getData(),
				List.class);
		allFields.put("certification", certificationFields);

		// Get Languages Fields
		CandidateResponseDTO.HttpResponse languages = formSubmissionAPIClient
				.getFormFieldNameList("candidate_languages");
		List<HashMap<String, String>> languagesFields = MappingUtil.mapClientBodyToClass(languages.getData(),
				List.class);
		allFields.put("languages", languagesFields);

		// Get Employer Details Fields
		CandidateResponseDTO.HttpResponse employerDetails = formSubmissionAPIClient
				.getFormFieldNameList("candidate_employer_details");
		List<HashMap<String, String>> employerDetailsFields = MappingUtil
				.mapClientBodyToClass(employerDetails.getData(), List.class);
		allFields.put("employerDetails", employerDetailsFields);

		// Get Documents Fields
		CandidateResponseDTO.HttpResponse documents = formSubmissionAPIClient
				.getFormFieldNameList("candidate_documents");
		List<HashMap<String, String>> documentsFields = MappingUtil.mapClientBodyToClass(documents.getData(),
				List.class);
		allFields.put("documents", documentsFields);

		return allFields;
	}

	@Override
	public HashMap<String, Object> updateCandidateEmbeddings(Integer candidateId) {
		HashMap<String, Object> candidateHashMapData = getCandidateByIdDataAll(candidateId);
		// Convert it into a json string
		String candidateDataJsonString = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
//			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			candidateDataJsonString = mapper.writeValueAsString(candidateHashMapData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		String allDetails = extractAllDetails(MappingUtil.convertJSONStringToJsonNode(candidateDataJsonString));
		System.out.println("All Details: " + allDetails);

		// Get Basic Info Embedding
		String basicInfo = extractBasicInfoDetailsIncludingSkills(
				MappingUtil.convertJSONStringToJsonNode(candidateDataJsonString));
		System.out.println("Basic Info: " + basicInfo);

		// Get education details
		String educationDetails = extractEducationDetails(
				MappingUtil.convertJSONStringToJsonNode(candidateDataJsonString));
		System.out.println("Education Details: " + educationDetails);

		// Get work experience details
		String workExperienceDetails = extractWorkExperienceDetails(
				MappingUtil.convertJSONStringToJsonNode(candidateDataJsonString));
		System.out.println("Work Experience Details: " + workExperienceDetails);

		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		embeddingRequestDTO.setText(allDetails);

		CandidateResponseDTO.HttpResponse candidateEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO candidateEmbeddingData = MappingUtil
				.mapClientBodyToClass(candidateEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		System.out.println("Embedding Data: " + candidateEmbeddingData.getEmbedding());

		// Update the candidate with the embedding
		candidateRepository.updateVector(candidateId.longValue(), "candidate_embeddings",
				candidateEmbeddingData.getEmbedding());

		// Get Basic Info Embedding
		EmbeddingRequestDTO basicInfoEmbeddingRequestDTO = new EmbeddingRequestDTO();
		basicInfoEmbeddingRequestDTO.setText(basicInfo);
		CandidateResponseDTO.HttpResponse basicInfoEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(basicInfoEmbeddingRequestDTO);
		EmbeddingResponseDTO basicInfoEmbeddingData = MappingUtil
				.mapClientBodyToClass(basicInfoEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		// Get education details embedding
		EmbeddingRequestDTO educationDetailsEmbeddingRequestDTO = new EmbeddingRequestDTO();
		educationDetailsEmbeddingRequestDTO.setText(educationDetails);
		CandidateResponseDTO.HttpResponse educationDetailsEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(educationDetailsEmbeddingRequestDTO);
		EmbeddingResponseDTO educationDetailsEmbeddingData = MappingUtil
				.mapClientBodyToClass(educationDetailsEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		// Get work experience details embedding
		EmbeddingRequestDTO workExperienceDetailsEmbeddingRequestDTO = new EmbeddingRequestDTO();
		workExperienceDetailsEmbeddingRequestDTO.setText(workExperienceDetails);
		CandidateResponseDTO.HttpResponse workExperienceDetailsEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(workExperienceDetailsEmbeddingRequestDTO);
		EmbeddingResponseDTO workExperienceDetailsEmbeddingData = MappingUtil
				.mapClientBodyToClass(workExperienceDetailsEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		// Update the candidate with basic info embedding
		candidateRepository.updateVector(candidateId.longValue(), "basic_info_embeddings",
				basicInfoEmbeddingData.getEmbedding());

		// Update candidate education details embedding
		candidateRepository.updateVector(candidateId.longValue(), "education_embeddings",
				educationDetailsEmbeddingData.getEmbedding());

		// Update the candidate with basic info embedding
		candidateRepository.updateVector(candidateId.longValue(), "work_experiences_embeddings",
				workExperienceDetailsEmbeddingData.getEmbedding());

		return candidateHashMapData;
	}

	@Override
	public List<CandidateJobSimilaritySearchResponseDTO> getCandidateJobSimilaritySearch(
			CandidateJobSimilaritySearchRequestDTO candidateJobSimilaritySearchRequestDTO) {
		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		System.out.println(
				"Job Description: " + removeStopWords(candidateJobSimilaritySearchRequestDTO.getJobDescription()));
		embeddingRequestDTO.setText(removeStopWords(candidateJobSimilaritySearchRequestDTO.getJobDescription()));
		CandidateResponseDTO.HttpResponse jobEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO jobEmbeddingData = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
				EmbeddingResponseDTO.class);
//		return candidateRepository.findSimilarSumScoresWithJobDescription(jobEmbeddingData.getEmbedding());
		return candidateRepository.findSimilarEmbeddingsCosine(jobEmbeddingData.getEmbedding(), "candidate_embeddings");
	}

	private CandidateListingDataDTO candidateEntityToCandidateNewListingDataDTO(CandidateEntity candidateEntity) {
		CandidateListingDataDTO candidateListingDataDTO = new CandidateListingDataDTO(candidateEntity);
		// Get created by User data from user microservice
		CandidateResponseDTO.HttpResponse createUserResponse = userAPIClient
				.getUserById(candidateEntity.getCreatedBy());
		CandidateResponseDTO.UserResponseDTO createUserData = MappingUtil
				.mapClientBodyToClass(createUserResponse.getData(), CandidateResponseDTO.UserResponseDTO.class);
		candidateListingDataDTO.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
		CandidateResponseDTO.HttpResponse updateUserResponse = userAPIClient
				.getUserById(candidateEntity.getUpdatedBy());
		CandidateResponseDTO.UserResponseDTO updateUserData = MappingUtil
				.mapClientBodyToClass(updateUserResponse.getData(), CandidateResponseDTO.UserResponseDTO.class);
		candidateListingDataDTO.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
		return candidateListingDataDTO;
	}

	// Candidate Listing page with search
//	@Override
//	public CandidateListingNewResponseDTO getCandidateListingPageWithSearch(Integer page, Integer size, String sortBy,
//			String sortDirection, String searchTerm, List<String> searchFields) {
//		// Get sort direction
//		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
//		if (sortDirection != null) {
//			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//		}
//		if (sortBy == null) {
//			sortBy = "updated_at";
//			direction = Sort.Direction.DESC;
//		}
//		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
//		Page<CandidateNewEntity> candidateEntitiesPage = null;
//		try {
//			candidateEntitiesPage = candidateNewRepository.findAllByOrderByAndSearchNumeric(getUserId(), false, false,
//					true, pageRequest, searchFields, searchTerm);
//
//		} catch (Exception e) {
//			candidateEntitiesPage = candidateNewRepository.findAllByOrderByAndSearchString(getUserId(), false, false,
//					true, pageRequest, searchFields, searchTerm);
//		}
//		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
//	}

	private CandidateListingResponseDTO pageCandidateListingToCandidateListingResponseDTO(
			Page<CandidateEntity> candidateNewEntitiesPage) {
		CandidateListingResponseDTO candidateListingResponseDTO = new CandidateListingResponseDTO();
		candidateListingResponseDTO.setTotalPages(candidateNewEntitiesPage.getTotalPages());
		candidateListingResponseDTO.setTotalElements(candidateNewEntitiesPage.getTotalElements());
		candidateListingResponseDTO.setPage(candidateNewEntitiesPage.getNumber());
		candidateListingResponseDTO.setPageSize(candidateNewEntitiesPage.getSize());
		List<CandidateListingDataDTO> candidateListingDataDTOS = new ArrayList<>();
		candidateListingDataDTOS = candidateNewEntitiesPage.getContent().stream().map(candidateNewEntity -> {
			CandidateListingDataDTO candidateListingDataDTO = new CandidateListingDataDTO(candidateNewEntity);

			// Get created by User data from user service
			CandidateResponseDTO.HttpResponse userResponse = userAPIClient
					.getUserById(candidateNewEntity.getCreatedBy());
			CandidateResponseDTO.UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
					CandidateResponseDTO.UserResponseDTO.class);
			candidateListingDataDTO.setCreatedByName(userData.getFirstName() + " " + userData.getLastName());

			// Get updated by User data from user service
			CandidateResponseDTO.HttpResponse updatedByUserResponse = userAPIClient
					.getUserById(candidateNewEntity.getUpdatedBy());
			CandidateResponseDTO.UserResponseDTO updatedByUserData = MappingUtil
					.mapClientBodyToClass(updatedByUserResponse.getData(), CandidateResponseDTO.UserResponseDTO.class);
			candidateListingDataDTO
					.setUpdatedByName(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
			return candidateListingDataDTO;
		}).toList();
		candidateListingResponseDTO.setCandidates(candidateListingDataDTOS);
		return candidateListingResponseDTO;
	}

//	@Override
//	public CandidateListingNewResponseDTO getCandidateListingPage(Integer page, Integer size, String sortBy,
//			String sortDirection) {
//		// Get sort direction
//		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
//		if (sortDirection != null && !sortDirection.isEmpty()) {
//			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//		}
//		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
//			sortBy = "updated_at";
//			direction = Sort.Direction.DESC;
//		}
//		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
//		Page<CandidateNewEntity> candidateEntitiesPage = null;
//		// Try with numeric first else try with string (jsonb)
//		try {
//			candidateEntitiesPage = candidateNewRepository.findAllByOrderByNumeric(getUserId(), false, false, true,
//					pageRequest);
//		} catch (Exception e) {
//			candidateEntitiesPage = candidateNewRepository.findAllByOrderByString(getUserId(), false, false, true,
//					pageRequest);
//		}
//		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
//	}

	@Override
	public CandidateListingResponseDTO getCandidateListingPage(Integer page, Integer size, String sortBy,
			String sortDirection, Boolean getAll) {
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
		Page<CandidateEntity> candidateEntitiesPage = null;
		// Try with numeric first else try with string (jsonb)
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}
		try {
			candidateEntitiesPage = candidateRepository.findAllByOrderByNumericWithUserIds(userIds, false, false, true,
					pageRequest);
		} catch (Exception e) {
			candidateEntitiesPage = candidateRepository.findAllByOrderByStringWithUserIds(userIds, false, false, true,
					pageRequest);
		}
		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
	}

	@Override
	public CandidateListingResponseDTO getCandidateListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields, Boolean getAll) {
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
		Page<CandidateEntity> candidateEntitiesPage = null;
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}
		try {
			candidateEntitiesPage = candidateRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds, false,
					false, true, pageRequest, searchFields, searchTerm);
		} catch (Exception e) {
			candidateEntitiesPage = candidateRepository.findAllByOrderByAndSearchStringWithUserIds(userIds, false,
					false, true, pageRequest, searchFields, searchTerm);
		}
		return pageCandidateListingToCandidateListingResponseDTO(candidateEntitiesPage);
	}

	@Override
	public Page<CandidateEntityWithSimilarity> getCandidateListingPageWithSimilaritySearch(
			CandidateListingRequestDTO candidateListingRequestDTO) {
		Integer page = candidateListingRequestDTO.getPage();
		Integer size = candidateListingRequestDTO.getPageSize();
		String sortBy = candidateListingRequestDTO.getSortBy();
		String sortDirection = candidateListingRequestDTO.getSortDirection();
		String searchTerm = candidateListingRequestDTO.getSearchTerm();
		Long jobId = candidateListingRequestDTO.getJobId();
		System.out.println("Job Id: " + jobId);
		System.out.println("Page: " + page);
		System.out.println("PageSize: " + size);
		System.out.println("SortBy: " + sortBy);
		System.out.println("SortDirection: " + sortDirection);
		System.out.println("SearchTerm: " + searchTerm);

		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "cosine_similarity";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		// Get the job description
		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();

//		embeddingRequestDTO.setText(removeStopWords("Need web designer with 5 years of experience"));
		embeddingRequestDTO.setText(removeStopWords("Need aerospace engineer who is good in aerodynamics"));
		CandidateResponseDTO.HttpResponse jobEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO jobEmbeddingData = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
				EmbeddingResponseDTO.class);
		List<Float> jobEmbedding = jobEmbeddingData.getEmbedding();

		Page<CandidateEntityWithSimilarity> result = candidateRepository.findAllByOrderByStringWithUserIdsAndSimilaritySearch(
				userUtil.getUsersIdUnderManager(), false, false, true, pageRequest, jobEmbedding);

		return result;
	}

	// @Override
//	public List<CandidateNewEntity> getAllCandidatesWithSearch(String query) {
//		List<CandidateNewEntity>candidateEntities = candidateNewRepository.getAllCandidatesWithSearch(query, getUserId(), false, false);
//		return candidateEntities;
//	}

	public static String extractRelevantDetails(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long totalExperienceMillis = 0;

		// Basic Info and Profile Summary
		JsonNode basicInfo = candidate.get("basicInfo");
		if (basicInfo != null) { // Check if basicInfo is not null
			String profileSummary = basicInfo.get("profileSummary") != null ? basicInfo.get("profileSummary").asText("")
					: "";
			details.append("Profile Summary: ").append(profileSummary).append("\n");
		}

//		// Work Experience
//		details.append("Work Experience:\n");
//		JsonNode workExperiences = candidate.get("workExperiences");
//		if (workExperiences != null && workExperiences.isArray()) {
//			for (JsonNode item : workExperiences) {
//				String title = item.get("title") != null ? item.get("title").asText("") : "";
//				String companyName = item.get("companyName") != null ? item.get("companyName").asText("") : "";
//				String description = item.get("description") != null ? item.get("description").asText("") : "";
//				details.append(title).append(" at ").append(companyName).append(", ").append(description).append("\n");
//			}
//		}

		// Work Experience and calculating total experience
		details.append("Work Experience:\n");
		JsonNode workExperiences = candidate.get("workExperiences");
		if (workExperiences != null && workExperiences.isArray()) {
			for (JsonNode item : workExperiences) {
				String title = item.get("title") != null ? item.get("title").asText("") : "";
				String companyName = item.get("companyName") != null ? item.get("companyName").asText("") : "";
				String description = item.get("description") != null ? item.get("description").asText("") : "";
				String startDate = item.get("startDate").asText("");
				String endDate = item.get("endDate").asText("");

				// Calculate experience duration for each job
				try {
					Date start = dateFormat.parse(startDate);
					Date end = dateFormat.parse(endDate);
					long experienceDurationMillis = end.getTime() - start.getTime();
					totalExperienceMillis += experienceDurationMillis;
					long experienceYears = experienceDurationMillis / (365 * 24 * 60 * 60 * 1000L); // Convert
																									// milliseconds to
																									// years
					details.append(title).append(" at ").append(companyName).append(" (").append(experienceYears)
							.append(" years), ").append(description).append("\n");
				} catch (ParseException e) {
					e.printStackTrace(); // Handle date parsing exception
				}
			}
		}

		// Append total years of experience
		long totalExperienceYears = totalExperienceMillis / (365 * 24 * 60 * 60 * 1000L);
		details.append("Total Years of Experience: ").append(totalExperienceYears).append("\n");

		// Education Details
		details.append("Education Details:\n");
		JsonNode educationDetails = candidate.get("educationDetails");
		if (educationDetails != null && educationDetails.isArray()) {
			for (JsonNode item : educationDetails) {
				String qualification = item.get("qualification") != null ? item.get("qualification").asText("") : "";
				String fieldOfStudy = item.get("fieldOfStudy") != null ? item.get("fieldOfStudy").asText("") : "";
				String institution = item.get("institution") != null ? item.get("institution").asText("") : "";
				details.append(qualification).append(" in ").append(fieldOfStudy).append(", ").append(institution)
						.append("\n");
			}
		}

		// Skills
		if (basicInfo != null) {
			String primarySkills = basicInfo.get("primarySkills") != null ? basicInfo.get("primarySkills").asText("")
					: "";
			String secondarySkills = basicInfo.get("secondarySkills") != null
					? basicInfo.get("secondarySkills").asText("")
					: "";
			if (!primarySkills.isEmpty() || !secondarySkills.isEmpty()) {
				details.append("Skills: ").append(primarySkills);
				if (!primarySkills.isEmpty() && !secondarySkills.isEmpty()) {
					details.append(", ");
				}
				details.append(secondarySkills).append("\n");
			}
		}

		// Certifications
		details.append("Certifications:\n");
		JsonNode certifications = candidate.get("certification");
		if (certifications != null && certifications.isArray()) {
			for (JsonNode item : certifications) {
				String certification = item.get("certification") != null ? item.get("certification").asText("") : "";
				details.append(certification).append("\n");
			}
		}

		// Skills
		details.append("Skills:\n");
		if (basicInfo != null) {
			String primarySkills = basicInfo.get("primarySkills") != null ? basicInfo.get("primarySkills").asText("")
					: "";
			String secondarySkills = basicInfo.get("secondarySkills") != null
					? basicInfo.get("secondarySkills").asText("")
					: "";
			String skill1 = basicInfo.get("skill1") != null ? basicInfo.get("skill1").asText("") : "";
			String skill2 = basicInfo.get("skill2") != null ? basicInfo.get("skill2").asText("") : "";
			String skill3 = basicInfo.get("skill3") != null ? basicInfo.get("skill3").asText("") : "";
			List<String> skillsList = new ArrayList<>();
			if (!primarySkills.isEmpty())
				skillsList.add(primarySkills);
			if (!secondarySkills.isEmpty())
				skillsList.add(secondarySkills);
			if (!skill1.isEmpty())
				skillsList.add(skill1);
			if (!skill2.isEmpty())
				skillsList.add(skill2);
			if (!skill3.isEmpty())
				skillsList.add(skill3);

			// Join all skills with comma separation and append
			details.append(String.join(", ", skillsList)).append("\n");
		}

		// Languages
		details.append("Languages:\n");
		JsonNode languages = candidate.get("languages");
		if (languages != null && languages.isArray()) {
			for (JsonNode item : languages) {
				String language = item.get("language") != null ? item.get("language").asText("") : "";
				details.append(language).append("\n");
			}
		}

		return details.toString();
	}

	public String extractBasicInfoDetailsIncludingSkills(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		JsonNode basicInfo = candidate.get("basicInfo");
		if (basicInfo != null) { // Check if basicInfo is not null
			details.append("Candidate Basic Information:\n");

			// Basic Information
			if (basicInfo.has("firstName") && basicInfo.has("lastName")) {
				String firstName = basicInfo.get("firstName").asText("");
				String lastName = basicInfo.get("lastName").asText("");
				if (!firstName.isEmpty() || !lastName.isEmpty()) {
					details.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
				}
			}

			if (basicInfo.has("currentPositionTitle") && !basicInfo.get("currentPositionTitle").asText().isEmpty()) {
				details.append("Current Position: ").append(basicInfo.get("currentPositionTitle").asText())
						.append("\n");
			}

			if (basicInfo.has("currentLocation") && !basicInfo.get("currentLocation").asText().isEmpty()) {
				details.append("Current Location: ").append(basicInfo.get("currentLocation").asText()).append("\n");
			}

			if (basicInfo.has("candidateNationality") && !basicInfo.get("candidateNationality").asText().isEmpty()) {
				details.append("Candidate Nationality: ").append(basicInfo.get("candidateNationality").asText())
						.append("\n");
			}

			if (basicInfo.has("profileSummary") && !basicInfo.get("profileSummary").asText().isEmpty()) {
				details.append("Profile Summary: ").append(basicInfo.get("profileSummary").asText()).append("\n");
			}

			// Skills
			if (basicInfo.has("primarySkill") && !basicInfo.get("primarySkill").asText().isEmpty()) {
				details.append("Primary Skill: ").append(basicInfo.get("primarySkill").asText()).append("\n");
			}

			if (basicInfo.has("primarySkills") && !basicInfo.get("primarySkills").asText().isEmpty()) {
				details.append("Primary Skills: ").append(basicInfo.get("primarySkills").asText()).append("\n");
			}

			if (basicInfo.has("skill1") && !basicInfo.get("skill1").asText().isEmpty()) {
				details.append("Skill 1: ").append(basicInfo.get("skill1").asText()).append("\n");
			}

			if (basicInfo.has("skill2") && !basicInfo.get("skill2").asText().isEmpty()) {
				details.append("Skill 2: ").append(basicInfo.get("skill2").asText()).append("\n");
			}

			if (basicInfo.has("skill3") && !basicInfo.get("skill3").asText().isEmpty()) {
				details.append("Skill 3: ").append(basicInfo.get("skill3").asText()).append("\n");
			}

			if (basicInfo.has("secondarySkill") && !basicInfo.get("secondarySkill").asText().isEmpty()) {
				details.append("Secondary Skill: ").append(basicInfo.get("secondarySkill").asText()).append("\n");
			}

			if (basicInfo.has("secondarySkills") && !basicInfo.get("secondarySkills").asText().isEmpty()) {
				details.append("Secondary Skills: ").append(basicInfo.get("secondarySkills").asText()).append("\n");
			}

			// Languages
			JsonNode languages = candidate.get("languages");
			if (languages != null && languages.isArray() && languages.size() > 0) {
				details.append("Languages: ");
				for (JsonNode languageNode : languages) {
					if (languageNode.has("language") && !languageNode.get("language").asText().isEmpty()) {
						details.append(languageNode.get("language").asText()).append(", ");
					}
				}
				// Remove the last comma and space if there were any languages listed
				if (details.toString().endsWith(", ")) {
					details.setLength(details.length() - 2); // Remove the last two characters (comma and space)
				}
				details.append("\n");
			}
		}
		return details.toString();
	}

	public String extractEducationDetails(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		JsonNode educationDetails = candidate.get("educationDetails");
		if (educationDetails != null && educationDetails.isArray()) { // Check if educationDetails is not null and is an
																		// array
			details.append("Candidate Education Details:\n");
			for (JsonNode item : educationDetails) {
				StringBuilder educationSentence = new StringBuilder();
				String institution = item.has("institution") ? item.get("institution").asText("") : "";
				String fieldOfStudy = item.has("fieldOfStudy") ? item.get("fieldOfStudy").asText("") : "";
				String qualification = item.has("qualification") ? item.get("qualification").asText("") : "";
				String startDate = item.has("startDate") ? item.get("startDate").asText("") : "";
				String graduationDate = item.has("graudationDate") ? item.get("graudationDate").asText("") : "";
				String grade = item.has("grade") && !item.get("grade").asText().isEmpty()
						? ", with a grade of " + item.get("grade").asText()
						: ""; // Added check for empty
				String activities = item.has("activities") ? item.get("activities").asText("") : "";
				String description = item.has("description") ? item.get("description").asText("") : "";

				// Constructing a concise summary for each education entry
				if (!qualification.isEmpty()) {
					educationSentence.append("Achieved ").append(qualification);
					if (!fieldOfStudy.isEmpty()) {
						educationSentence.append(" in ").append(fieldOfStudy);
					}
					if (!institution.isEmpty()) {
						educationSentence.append(" from ").append(institution);
					}
					if (!startDate.isEmpty() || !graduationDate.isEmpty()) {
						educationSentence.append(", studied from ").append(startDate).append(" to ")
								.append(graduationDate);
					}
					educationSentence.append(grade).append(".\n");
				}

				// Append the constructed education sentence if it's not empty
				if (educationSentence.length() > 0) {
					details.append(educationSentence.toString());
				}

				// Keeping activities and description as provided, only if they are not empty
				if (!activities.isEmpty()) {
					details.append("Activities: ").append(activities).append(".\n");
				}

				if (!description.isEmpty()) {
					details.append("Description: ").append(description).append(".\n");
				}

				details.append("\n"); // Add an extra newline for spacing between entries
			}
		}
		return details.toString();
	}

	public String extractWorkExperienceDetails(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		JsonNode workExperiences = candidate.get("workExperiences");
		long totalMonths = 0; // For calculating total work experience
		if (workExperiences != null && workExperiences.isArray()) { // Check if workExperiences is not null and is an
																	// array
			details.append("Candidate Work Experience Details:\n");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			for (JsonNode item : workExperiences) {
				StringBuilder experienceSentence = new StringBuilder();
				String title = item.has("title") ? item.get("title").asText("") : "";
				String companyName = item.has("companyName") ? item.get("companyName").asText("") : "";
				String startDateStr = item.has("startDate") ? item.get("startDate").asText("") : "";
				String endDateStr = item.has("endDate") && !item.get("endDate").asText().equals("NaN-NaN-NaN")
						? item.get("endDate").asText()
						: "Present"; // Check for valid end date
				String description = item.has("description") ? item.get("description").asText("") : "";
				String projectSnippet = item.has("Projectsnippet") ? item.get("Projectsnippet").asText("") : "";
				LocalDate startDate = null, endDate = null;

				// Attempt to parse the start and end dates
				try {
					startDate = !startDateStr.isEmpty() ? LocalDate.parse(startDateStr, formatter) : null;
					endDate = !endDateStr.isEmpty() && !endDateStr.equals("Present")
							? LocalDate.parse(endDateStr, formatter)
							: LocalDate.now(); // Use current date if "Present"
				} catch (DateTimeParseException e) {
					// If parsing fails, leave the dates as null
				}

				// Calculate the duration of the work in months
				long monthsWorked = 0;
				if (startDate != null && endDate != null) {
					monthsWorked = ChronoUnit.MONTHS.between(startDate, endDate);
					totalMonths += monthsWorked; // Add to total work experience
				}

				// Constructing a concise summary for each work entry
				if (!title.isEmpty()) {
					experienceSentence.append("Worked as ").append(title);
					if (!companyName.isEmpty()) {
						experienceSentence.append(" at ").append(companyName);
					}
					if (monthsWorked > 0) {
						long years = monthsWorked / 12;
						long months = monthsWorked % 12;
						experienceSentence.append(" for ");
						if (years > 0) {
							experienceSentence.append(years).append(" years ");
						}
						if (months > 0) {
							experienceSentence.append(months).append(" months");
						}
					}
					experienceSentence.append(".\n");
				}

				if (!description.isEmpty()) {
					experienceSentence.append("Role involved: ").append(description).append(".\n");
				}

				if (!projectSnippet.isEmpty()) {
					experienceSentence.append("Key projects: ").append(projectSnippet).append(".\n");
				}

				// Append the constructed work experience sentence if it's not empty
				if (experienceSentence.length() > 0) {
					details.append(experienceSentence.toString()).append("\n");
				}
			}

			// Adding total work experience at the end
			long totalYears = totalMonths / 12;
			long totalRemainingMonths = totalMonths % 12;
			details.append("Total Work Experience: ").append(totalYears).append(" years and ")
					.append(totalRemainingMonths).append(" months.\n");
		}
		return details.toString();
	}

	// Combine all the details into a single string
	public String extractAllDetails(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		details.append(extractBasicInfoDetailsIncludingSkills(candidate));
		details.append(extractEducationDetails(candidate));
		details.append(extractWorkExperienceDetails(candidate));
		return details.toString();
	}

	// Assuming you have a set of stop words
	private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("i", "me", "my", "myself", "we", "our",
			"ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she",
			"her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what",
			"which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been",
			"being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if",
			"or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between",
			"into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out",
			"on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why",
			"how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not",
			"only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should",
			"now"));

	public static String removeStopWords(String jobDescription) {
		// Tokenize the string and remove stop words
		return Arrays.stream(jobDescription.split("\\s+")).filter(word -> !STOP_WORDS.contains(word.toLowerCase()))
				.collect(Collectors.joining(" "));
	}

}
