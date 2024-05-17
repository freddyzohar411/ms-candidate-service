package com.avensys.rts.candidate.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.avensys.rts.candidate.APIClient.*;
import com.avensys.rts.candidate.constant.MessageConstants;
import com.avensys.rts.candidate.entity.CandidateEntity;
import com.avensys.rts.candidate.entity.CandidateEntityWithSimilarity;
import com.avensys.rts.candidate.entity.CustomFieldsEntity;
import com.avensys.rts.candidate.exception.DuplicateResourceException;
import com.avensys.rts.candidate.model.FieldInformation;
import com.avensys.rts.candidate.payloadnewrequest.*;
import com.avensys.rts.candidate.payloadnewresponse.*;
import com.avensys.rts.candidate.repository.CandidateCustomFieldsRepository;
import com.avensys.rts.candidate.repository.CandidateRepository;
import com.avensys.rts.candidate.util.*;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.transaction.Transactional;

@Service
public class CandidateServiceImpl implements CandidateService {

	private final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);

	private final String CANDIDATE_BASIC_INFO_ENTITY_TYPE = "candidate_basic_info";

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CandidateCustomFieldsRepository candidateCustomFieldsRepository;

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
	@Autowired
	private JobAPIClient jobAPIClient;

	@Override
	@Transactional
	public CandidateResponseDTO createCandidate(CandidateRequestDTO candidateRequestDTO) {
		LOG.info("Candidate create : Service");
		System.out.println("createCandidate" + candidateRequestDTO);
		String email = getEmailFromRequest(candidateRequestDTO);

		if (email != null && !email.isEmpty()) {
			if (candidateRepository.existsByEmailAndNotDeleted(email)) {
				throw new ServiceException(messageSource.getMessage(MessageConstants.CANDIDATE_EXIST, null,
						LocaleContextHolder.getLocale()));
			}
		}
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

		String newEmail = getEmailFromRequest(candidateRequestDTO);
		if (newEmail != null && !newEmail.isEmpty()) {
			String currentEmail = candidateEntity.getCandidateSubmissionData().get("email").asText();
			if (!newEmail.equals(currentEmail) && candidateRepository.existsByEmailAndNotDeleted(newEmail)) {
				throw new ServiceException(messageSource.getMessage(MessageConstants.CANDIDATE_EXIST, null,
						LocaleContextHolder.getLocale()));
			}
		}

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
//		List<CandidateEntity> candidateEntities = candidateRepository
//				.findAllByUserIdsAndDeleted(userUtil.getUsersIdUnderManager(), false, true);
		List<CandidateEntity> candidateEntities = candidateRepository.findAllByIsDraftAndIsDeletedAndIsActive(false,
				false, true);

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

		// Convert HashMap to JSON String
		JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(candidateHashMapData);
		CandidateDataExtractionUtil.printJSON(candidateDataJsonNode);

		String candidateDetails = CandidateDataExtractionUtil.extractAllDetails(candidateDataJsonNode);

		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		embeddingRequestDTO.setText(TextProcessingUtil.removeStopWords(candidateDetails));

		CandidateResponseDTO.HttpResponse candidateEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO candidateEmbeddingData = MappingUtil
				.mapClientBodyToClass(candidateEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		// Update the candidate with the embedding
		candidateRepository.updateVector(candidateId.longValue(), "candidate_embeddings",
				candidateEmbeddingData.getEmbedding());

		// Get candidate
		CandidateEntity candidateEntity = candidateRepository.findByIdAndDeleted(candidateId, false, true)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		// Update the candidate with the complete info
		candidateEntity.setCandidateCompleteInfo(candidateDetails);
		candidateRepository.save(candidateEntity);

		return candidateHashMapData;
	}

	@Override
	public List<CandidateJobSimilaritySearchResponseDTO> getCandidateJobSimilaritySearch(
			CandidateJobSimilaritySearchRequestDTO candidateJobSimilaritySearchRequestDTO) {
		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		embeddingRequestDTO.setText(
				TextProcessingUtil.removeStopWords(candidateJobSimilaritySearchRequestDTO.getJobDescription()));
		CandidateResponseDTO.HttpResponse jobEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO jobEmbeddingData = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
				EmbeddingResponseDTO.class);
		List<CandidateJobSimilaritySearchResponseDTO> candidateJobSimilaritySearchResponseDTOList = candidateRepository
				.findSimilarEmbeddingsCosine(jobEmbeddingData.getEmbedding(), "candidate_embeddings");
		return candidateJobSimilaritySearchResponseDTOList;
	}

	@Override
	public CandidateMatchingDetailsResponseDTO getMatchCandidateToJobData(Integer candidateId, Long jobId) {
		RequestAttributes parentContext = RequestContextHolder.getRequestAttributes();
		// Get the job data
		CandidateResponseDTO.HttpResponse jobResponse = jobAPIClient.getJobByIdDataAll(jobId);
		HashMap<String, Object> jobData = MappingUtil.mapClientBodyToClass(jobResponse.getData(), HashMap.class);
		JsonNode jobJsonNode = MappingUtil.convertHashMapToJsonNode(jobData);
		JobDataExtractionUtil.printJSON(jobJsonNode);
		String jobDataAll = JobDataExtractionUtil.extractJobInfo(jobJsonNode);

		// Get job extracted data
		Set<String> jobQualifications = JobDataExtractionUtil.extractJobQualifications(jobJsonNode);
		Set<String> jobLanguages = JobDataExtractionUtil.extractJobLanguages(jobJsonNode);
		String jobDescription = JobDataExtractionUtil.extractJobDescription(jobJsonNode);
		Set<String> jobTitles = JobDataExtractionUtil.extractJobTitle(jobJsonNode);
		String jobCountry = JobDataExtractionUtil.extractJobCountry(jobJsonNode);

		// Get candidate data
		HashMap<String, Object> candidateData = getCandidateByIdDataAll(candidateId);
		JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(candidateData);
		Set<String> candidateQualifications = CandidateDataExtractionUtil
				.extractCandidateEducationQualificationsSet(candidateDataJsonNode);
		Set<String> candidateLanguages = CandidateDataExtractionUtil
				.extractCandidateLanguagesSet(candidateDataJsonNode);
		Set<String> candidateSkills = CandidateDataExtractionUtil.extractCandidateSkillsSet(candidateDataJsonNode);
		Set<String> candidateJobTitles = CandidateDataExtractionUtil
				.extractCandidateWorkTitlesSet(candidateDataJsonNode);
		String candidateNationality = CandidateDataExtractionUtil.extractCandidateNationality(candidateDataJsonNode);
		String candidateDetails = CandidateDataExtractionUtil.extractAllDetails(candidateDataJsonNode);
		Set<String> candidateFieldOfStudy = CandidateDataExtractionUtil
				.extractCandidateFieldOfStudySet(candidateDataJsonNode);

		CandidateMatchingDetailsResponseDTO candidateMatchingDetailsResponseDTO = new CandidateMatchingDetailsResponseDTO();
		CompletableFuture<Void> candidateFuture = CompletableFuture.runAsync(() -> {
			// Use pre-fetched data for processing

			EmbeddingListCompareRequestDTO qualificationRequestDTO = new EmbeddingListCompareRequestDTO();
			qualificationRequestDTO.setJobAttributes(jobQualifications);
			qualificationRequestDTO.setCandidateAttributes(candidateQualifications);

			EmbeddingListCompareRequestDTO languageRequestDTO = new EmbeddingListCompareRequestDTO();
			languageRequestDTO.setJobAttributes(jobLanguages);
			languageRequestDTO.setCandidateAttributes(candidateLanguages);

			EmbeddingListCompareRequestDTO jobTitlesRequestDTO = new EmbeddingListCompareRequestDTO();
			jobTitlesRequestDTO.setJobAttributes(jobTitles);
			jobTitlesRequestDTO.setCandidateAttributes(candidateJobTitles);

			EmbeddingListTextCompareRequestDTO jobSkillsRequestDTO = new EmbeddingListTextCompareRequestDTO();
			jobSkillsRequestDTO.setJobAttributes(jobDescription);
			jobSkillsRequestDTO.setCandidateAttributes(candidateSkills);

			EmbeddingTextCompareRequestDTO generalRequestDTO = new EmbeddingTextCompareRequestDTO();
			generalRequestDTO.setJobAttributes(jobDataAll);
			generalRequestDTO.setCandidateAttributes(candidateDetails);
			generalRequestDTO.setModelName("all-MiniLM-L6-v2");

			EmbeddingListTextCompareRequestDTO fieldOfStudyRequestDTO = new EmbeddingListTextCompareRequestDTO();
			fieldOfStudyRequestDTO.setJobAttributes(jobDataAll);
			fieldOfStudyRequestDTO.setCandidateAttributes(candidateFieldOfStudy);

			List<CompletableFuture<EmbeddingListCompareResponseDTO>> futures = new ArrayList<>();
			futures.add(compareEmbeddingsListAsyncMan(qualificationRequestDTO, parentContext));
			futures.add(compareEmbeddingsListAsyncMan(languageRequestDTO, parentContext));
			futures.add(compareEmbeddingsListTextAsyncMan(jobSkillsRequestDTO, parentContext));
			futures.add(compareEmbeddingsListAsyncMan(jobTitlesRequestDTO, parentContext));
			futures.add(compareEmbeddingsTextAsyncMan(generalRequestDTO, parentContext));
			futures.add(compareEmbeddingsListTextAsyncMan(fieldOfStudyRequestDTO, parentContext));
			// Add more futures as needed

			CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

			allFutures.thenRun(() -> {
				// Process the results
				EmbeddingListCompareResponseDTO qualificationResponse = futures.get(0).join();
				EmbeddingListCompareResponseDTO languageResponse = futures.get(1).join();
				EmbeddingListCompareResponseDTO jobSkillsResponse = futures.get(2).join();
				EmbeddingListCompareResponseDTO jobTitlesResponse = futures.get(3).join();
				EmbeddingListCompareResponseDTO generalResponse = futures.get(4).join();
				EmbeddingListCompareResponseDTO fieldOfStudyResponse = futures.get(5).join();

				// Add to the candidate entity with similarity
				candidateMatchingDetailsResponseDTO.setQualificationScoreDetails(
						JSONUtil.convertObjectToJsonNode(qualificationResponse.getSimilar_attributes()));
				candidateMatchingDetailsResponseDTO.setLanguageScoreDetails(
						JSONUtil.convertObjectToJsonNode(languageResponse.getSimilar_attributes()));
				candidateMatchingDetailsResponseDTO.setSkillsScoreDetails(
						JSONUtil.convertObjectToJsonNode(jobSkillsResponse.getSimilar_attributes()));
				candidateMatchingDetailsResponseDTO.setJobTitleScoreDetails(
						JSONUtil.convertObjectToJsonNode(jobTitlesResponse.getSimilar_attributes()));
				candidateMatchingDetailsResponseDTO.setGeneralScoreDetails(
						JSONUtil.convertObjectToJsonNode(generalResponse.getSimilar_attributes()));
				candidateMatchingDetailsResponseDTO.setFieldOfStudyScoreDetails(
						JSONUtil.convertObjectToJsonNode(fieldOfStudyResponse.getSimilar_attributes()));

				// Set Normalized Score
				candidateMatchingDetailsResponseDTO.setNormalizedQualificationScore(qualificationResponse.getNormalized_score());
				candidateMatchingDetailsResponseDTO.setNormalizedLanguageScore(languageResponse.getNormalized_score());
				candidateMatchingDetailsResponseDTO.setNormalizedSkillsScore(jobSkillsResponse.getNormalized_score());
				candidateMatchingDetailsResponseDTO.setNormalizedJobTitleScore(jobTitlesResponse.getNormalized_score());
				candidateMatchingDetailsResponseDTO.setNormalizedGeneralScore(generalResponse.getNormalized_score());
				candidateMatchingDetailsResponseDTO.setNormalizedFieldOfStudyScore(fieldOfStudyResponse.getNormalized_score());

				// Set Similarity Score
				candidateMatchingDetailsResponseDTO.setQualificationScore(qualificationResponse.getSimilarity_score());
				candidateMatchingDetailsResponseDTO.setLanguageScore(languageResponse.getSimilarity_score());
				candidateMatchingDetailsResponseDTO.setSkillsScore(jobSkillsResponse.getSimilarity_score());
				candidateMatchingDetailsResponseDTO.setJobTitleScore(jobTitlesResponse.getSimilarity_score());
				candidateMatchingDetailsResponseDTO.setGeneralScore(generalResponse.getSimilarity_score());
				candidateMatchingDetailsResponseDTO.setFieldOfStudyScore(fieldOfStudyResponse.getSimilarity_score());

				candidateMatchingDetailsResponseDTO.setCandidateId(candidateId.longValue());
				candidateMatchingDetailsResponseDTO.setJobId(jobId);

			}).join();

		});

		// Wait for all asynchronous operations to complete
		candidateFuture.join();
		return candidateMatchingDetailsResponseDTO;
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

	// WORK ON THIS
	@Override
	public CandidateSimilarityListingResponseDTO getCandidateListingPageWithSimilaritySearch(
			CandidateListingRequestDTO candidateListingRequestDTO) throws ExecutionException, InterruptedException {
		Integer page = candidateListingRequestDTO.getPage();
		Integer size = candidateListingRequestDTO.getPageSize();
		String sortBy = candidateListingRequestDTO.getSortBy();
		String sortDirection = candidateListingRequestDTO.getSortDirection();
		Long jobId = candidateListingRequestDTO.getJobId();
		String customQuery = candidateListingRequestDTO.getCustomQuery();

		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "cosine_similarity";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		List<Float> jobEmbeddingData = new ArrayList<>();
		if (customQuery != null && !customQuery.isEmpty()) {
			// Get Embedding for custom query
			EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
			embeddingRequestDTO.setText(customQuery);
			CandidateResponseDTO.HttpResponse customQueryEmbeddingResponse = embeddingAPIClient
					.getEmbeddingSinglePy(embeddingRequestDTO);
			EmbeddingResponseDTO customQueryEmbedding = MappingUtil
					.mapClientBodyToClass(customQueryEmbeddingResponse.getData(), EmbeddingResponseDTO.class);
			jobEmbeddingData = customQueryEmbedding.getEmbedding();
		} else {
			// Get Job Embeddings
			CandidateResponseDTO.HttpResponse jobEmbeddingResponse = jobAPIClient.getEmbeddingsById(jobId, "default");
			EmbeddingResponseDTO jobEmbedding = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
					EmbeddingResponseDTO.class);
			jobEmbeddingData = jobEmbedding.getEmbedding();
		}

		Page<CandidateEntityWithSimilarity> candidateEntityWithSimilarityPage = null;

		Boolean isAdmin = userUtil.checkIsAdmin();
		List<Long> userIds = new ArrayList<>();
		if (!isAdmin) {
			userIds = userUtil.getUsersIdUnderManager();
		}

		try {
			candidateEntityWithSimilarityPage = candidateRepository
					.findAllByOrderByNumericWithUserIdsAndSimilaritySearch(userIds, false,
							false, true, pageRequest, jobEmbeddingData, true);
		} catch (Exception e) {
			candidateEntityWithSimilarityPage = candidateRepository
					.findAllByOrderByStringWithUserIdsAndSimilaritySearch(userIds, false,
							false, true, pageRequest, jobEmbeddingData, true);
		}

		// Special evaluation for each candidate compute the other score in using
		// concurrency
		// Use this getMatchCandidateToJobData function, set sort to true
//		 getSimilarityData(candidateEntityWithSimilarityPage, jobId);
//		getSimilarityData2(candidateEntityWithSimilarityPage, jobId);

		return candidateSimilarityPageToCandidateSimilarityListingResponse(candidateEntityWithSimilarityPage, false);
	}

	private CandidateMatchingDetailsResponseDTO getSimilarityData(Page<CandidateEntityWithSimilarity> candidatePage,
			Long jobId) {
		List<CandidateEntityWithSimilarity> candidateEntityWithSimilarityList = candidatePage.getContent();
		RequestAttributes parentContext = RequestContextHolder.getRequestAttributes();
		// Get the job data
		CandidateResponseDTO.HttpResponse jobResponse = jobAPIClient.getJobByIdDataAll(jobId);
		HashMap<String, Object> jobData = MappingUtil.mapClientBodyToClass(jobResponse.getData(), HashMap.class);
		JsonNode jobJsonNode = MappingUtil.convertHashMapToJsonNode(jobData);
		JobDataExtractionUtil.printJSON(jobJsonNode);
		String jobDataAll = JobDataExtractionUtil.extractJobInfo(jobJsonNode);

		// Get job extracted data
		Set<String> jobQualifications = JobDataExtractionUtil.extractJobQualifications(jobJsonNode);
		Set<String> jobLanguages = JobDataExtractionUtil.extractJobLanguages(jobJsonNode);
		String jobDescription = JobDataExtractionUtil.extractJobDescription(jobJsonNode);
		Set<String> jobTitles = JobDataExtractionUtil.extractJobTitle(jobJsonNode);
		String jobCountry = JobDataExtractionUtil.extractJobCountry(jobJsonNode);

		// Get All the cadnidateData first by loop and storing it
		List<HashMap<String, Object>> candidateDataList = new ArrayList<>();
		for (CandidateEntityWithSimilarity candidateEntityWithSimilarity : candidatePage.getContent()) {
			HashMap<String, Object> candidateData = getCandidateByIdDataAll(candidateEntityWithSimilarity.getId());
			candidateDataList.add(candidateData);
		}

		// Use concurrency to get the similarity data
		List<CompletableFuture<CandidateMatchingDetailsResponseDTO>> futures = candidateDataList.stream()
				.map(candidateData -> CompletableFuture.supplyAsync(() -> {
					JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(candidateData);
					Set<String> candidateQualifications = CandidateDataExtractionUtil
							.extractCandidateEducationQualificationsSet(candidateDataJsonNode);
//					Set<String> candidateLanguages = CandidateDataExtractionUtil
//							.extractCandidateLanguagesSet(candidateDataJsonNode);
					Set<String> candidateSkills = CandidateDataExtractionUtil
							.extractCandidateSkillsSet(candidateDataJsonNode);
					Set<String> candidateJobTitles = CandidateDataExtractionUtil
							.extractCandidateWorkTitlesSet(candidateDataJsonNode);
					String candidateDetails = CandidateDataExtractionUtil.extractAllDetails(candidateDataJsonNode);

					EmbeddingListCompareRequestDTO qualificationRequestDTO = new EmbeddingListCompareRequestDTO();
					qualificationRequestDTO.setJobAttributes(jobQualifications);
					qualificationRequestDTO.setCandidateAttributes(candidateQualifications);

//					EmbeddingListCompareRequestDTO languageRequestDTO = new EmbeddingListCompareRequestDTO();
//					languageRequestDTO.setJobAttributes(jobLanguages);
//					languageRequestDTO.setCandidateAttributes(candidateLanguages);

					EmbeddingListCompareRequestDTO jobTitlesRequestDTO = new EmbeddingListCompareRequestDTO();
					jobTitlesRequestDTO.setJobAttributes(jobTitles);
					jobTitlesRequestDTO.setCandidateAttributes(candidateJobTitles);

					EmbeddingListTextCompareRequestDTO jobSkillsRequestDTO = new EmbeddingListTextCompareRequestDTO();
					jobSkillsRequestDTO.setJobAttributes(jobDescription);
					jobSkillsRequestDTO.setCandidateAttributes(candidateSkills);

					EmbeddingTextCompareRequestDTO generalRequestDTO = new EmbeddingTextCompareRequestDTO();
					generalRequestDTO.setJobAttributes(jobDataAll);
					generalRequestDTO.setCandidateAttributes(candidateDetails);

					CompletableFuture<EmbeddingListCompareResponseDTO> qualificationFuture = compareEmbeddingsListAsyncMan(
							qualificationRequestDTO, parentContext);
//					CompletableFuture<EmbeddingListCompareResponseDTO> languageFuture = compareEmbeddingsListAsyncMan(
//							languageRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> jobTitlesFuture = compareEmbeddingsListAsyncMan(
							jobTitlesRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> jobSkillsFuture = compareEmbeddingsListTextAsyncMan(
							jobSkillsRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> generalFuture = compareEmbeddingsTextAsyncMan(
							generalRequestDTO, parentContext);

					return CompletableFuture.allOf(qualificationFuture, jobTitlesFuture, jobSkillsFuture, generalFuture)
							.thenApply(v -> {
								CandidateMatchingDetailsResponseDTO candidateMatchingDetailsResponseDTO = new CandidateMatchingDetailsResponseDTO();

								// Directly access future results without join(), since we are already in a
								// completion stage.
//								double jobSkillScore = jobSkillsFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double jobTitleScore = jobTitlesFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double qualificationScore = qualificationFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double generalScore = generalFuture.join().getSimilar_attributes() != null
//										? generalFuture.join().getSimilar_attributes().stream()
//												.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//														: attribute.getScore().doubleValue())
//												.sum()
//										: 0.0;

								double jobSkillScore = Optional.ofNullable(jobSkillsFuture.join().getSimilar_attributes())
										.map(List::stream)
										.orElseGet(Stream::empty)
										.mapToDouble(attribute -> Optional.ofNullable(attribute.getScore()).orElse(0.0))
										.sum();

								double jobTitleScore = Optional.ofNullable(jobTitlesFuture.join().getSimilar_attributes())
										.map(List::stream)
										.orElseGet(Stream::empty)
										.mapToDouble(attribute -> Optional.ofNullable(attribute.getScore()).orElse(0.0))
										.sum();

								double qualificationScore = Optional.ofNullable(qualificationFuture.join().getSimilar_attributes())
										.map(List::stream)
										.orElseGet(Stream::empty)
										.mapToDouble(attribute -> Optional.ofNullable(attribute.getScore()).orElse(0.0))
										.sum();

								double generalScore = Optional.ofNullable(generalFuture.join().getSimilar_attributes())
										.map(List::stream)
										.orElseGet(Stream::empty)
										.mapToDouble(attribute -> Optional.ofNullable(attribute.getScore()).orElse(0.0))
										.sum();

								// Set scores...
								candidateMatchingDetailsResponseDTO.setSkillsScore(jobSkillScore);
								candidateMatchingDetailsResponseDTO.setJobTitleScore(jobTitleScore);
								candidateMatchingDetailsResponseDTO.setQualificationScore(qualificationScore);
								candidateMatchingDetailsResponseDTO.setGeneralScore(generalScore);

								return candidateMatchingDetailsResponseDTO;
							});
					// This .thenCompose is crucial; it flattens the
					// CompletableFuture<CompletableFuture<T>> to CompletableFuture<T>
				}).thenCompose(Function.identity())).collect(Collectors.toList());

		// Wait for all futures to complete and collect the results
		List<CandidateMatchingDetailsResponseDTO> candidateMatchingDetailsResponseDTOList = futures.stream()
				.map(CompletableFuture::join).collect(Collectors.toList());

		// Normalize score between max and min for each section
		// Get the max and min for each section
		Double maxQualificationScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getQualificationScore).max().orElse(0.0);
		Double minQualificationScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getQualificationScore).min().orElse(0.0);
		Double maxSkillsScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getSkillsScore).max().orElse(0.0);
		Double minSkillsScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getSkillsScore).min().orElse(0.0);
		Double maxJobTitleScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getJobTitleScore).max().orElse(0.0);
		Double minJobTitleScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getJobTitleScore).min().orElse(0.0);
		Double maxGeneralScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getGeneralScore).max().orElse(0.0);
		Double minGeneralScore = candidateMatchingDetailsResponseDTOList.stream()
				.mapToDouble(CandidateMatchingDetailsResponseDTO::getGeneralScore).min().orElse(0.0);

		// Normalize the score
		for (CandidateMatchingDetailsResponseDTO ca : candidateMatchingDetailsResponseDTOList) {
			if (maxQualificationScore.equals(minQualificationScore)) {
				ca.setQualificationScore(0.0);
			} else {
				ca.setQualificationScore((ca.getQualificationScore() - minQualificationScore)
						/ (maxQualificationScore - minQualificationScore));
			}
			if (maxSkillsScore.equals(minSkillsScore)) {
				ca.setSkillsScore(0.0);
			} else {
				ca.setSkillsScore((ca.getSkillsScore() - minSkillsScore) / (maxSkillsScore - minSkillsScore));
			}
			if (maxJobTitleScore.equals(minJobTitleScore)) {
				ca.setJobTitleScore(0.0);
			} else {
				ca.setJobTitleScore((ca.getJobTitleScore() - minJobTitleScore) / (maxJobTitleScore - minJobTitleScore));
			}

			if (maxGeneralScore.equals(minGeneralScore)) {
				ca.setGeneralScore(0.0);
			} else {
				ca.setGeneralScore((ca.getGeneralScore() - minGeneralScore) / (maxGeneralScore - minGeneralScore));
			}

			Double preComputedScore = ca.getGeneralScore() * 0.2 + ca.getQualificationScore() * 0.2
					+ ca.getSkillsScore() * 0.3 + ca.getJobTitleScore() * 0.3;
			ca.setComputedScore(preComputedScore);

		}

		// Update the page content with all these data
		for (int i = 0; i < candidateEntityWithSimilarityList.size(); i++) {
			CandidateEntityWithSimilarity ca = candidateEntityWithSimilarityList.get(i);
			ca.setComputedScore(candidateMatchingDetailsResponseDTOList.get(i).getComputedScore() * 0.4
					+ ca.getSimilarityScore() * 0.6);
		}
		return null;
	}

	private CandidateMatchingDetailsResponseDTO getSimilarityData2(Page<CandidateEntityWithSimilarity> candidatePage,
			Long jobId) {
		List<CandidateEntityWithSimilarity> candidateEntityWithSimilarityList = candidatePage.getContent();
		RequestAttributes parentContext = RequestContextHolder.getRequestAttributes();
		// Get the job data
		CandidateResponseDTO.HttpResponse jobResponse = jobAPIClient.getJobByIdDataAll(jobId);
		HashMap<String, Object> jobData = MappingUtil.mapClientBodyToClass(jobResponse.getData(), HashMap.class);
		JsonNode jobJsonNode = MappingUtil.convertHashMapToJsonNode(jobData);
		JobDataExtractionUtil.printJSON(jobJsonNode);
		String jobDataAll = JobDataExtractionUtil.extractJobInfo(jobJsonNode);

		// Get job extracted data
		Set<String> jobQualifications = JobDataExtractionUtil.extractJobQualifications(jobJsonNode);
		Set<String> jobLanguages = JobDataExtractionUtil.extractJobLanguages(jobJsonNode);
		String jobDescription = JobDataExtractionUtil.extractJobDescription(jobJsonNode);
		Set<String> jobTitles = JobDataExtractionUtil.extractJobTitle(jobJsonNode);
		String jobCountry = JobDataExtractionUtil.extractJobCountry(jobJsonNode);

		// Get All the cadnidateData first by loop and storing it
		List<HashMap<String, Object>> candidateDataList = new ArrayList<>();
		for (CandidateEntityWithSimilarity candidateEntityWithSimilarity : candidatePage.getContent()) {
			HashMap<String, Object> candidateData = getCandidateByIdDataAll(candidateEntityWithSimilarity.getId());
			candidateDataList.add(candidateData);
		}

		// Use concurrency to get the similarity data
		List<CompletableFuture<CandidateMatchingDetailsResponseDTO>> futures = candidateDataList.stream()
				.map(candidateData -> CompletableFuture.supplyAsync(() -> {
					JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(candidateData);
					Set<String> candidateQualifications = CandidateDataExtractionUtil
							.extractCandidateEducationQualificationsSet(candidateDataJsonNode);
					//					Set<String> candidateLanguages = CandidateDataExtractionUtil
					//							.extractCandidateLanguagesSet(candidateDataJsonNode);
					Set<String> candidateSkills = CandidateDataExtractionUtil
							.extractCandidateSkillsSet(candidateDataJsonNode);
					Set<String> candidateJobTitles = CandidateDataExtractionUtil
							.extractCandidateWorkTitlesSet(candidateDataJsonNode);
					String candidateDetails = CandidateDataExtractionUtil.extractAllDetails(candidateDataJsonNode);

					EmbeddingListCompareRequestDTO qualificationRequestDTO = new EmbeddingListCompareRequestDTO();
					qualificationRequestDTO.setJobAttributes(jobQualifications);
					qualificationRequestDTO.setCandidateAttributes(candidateQualifications);

					//					EmbeddingListCompareRequestDTO languageRequestDTO = new EmbeddingListCompareRequestDTO();
					//					languageRequestDTO.setJobAttributes(jobLanguages);
					//					languageRequestDTO.setCandidateAttributes(candidateLanguages);

					EmbeddingListCompareRequestDTO jobTitlesRequestDTO = new EmbeddingListCompareRequestDTO();
					jobTitlesRequestDTO.setJobAttributes(jobTitles);
					jobTitlesRequestDTO.setCandidateAttributes(candidateJobTitles);

					EmbeddingListTextCompareRequestDTO jobSkillsRequestDTO = new EmbeddingListTextCompareRequestDTO();
					jobSkillsRequestDTO.setJobAttributes(jobDescription);
					jobSkillsRequestDTO.setCandidateAttributes(candidateSkills);

					EmbeddingTextCompareRequestDTO generalRequestDTO = new EmbeddingTextCompareRequestDTO();
					generalRequestDTO.setJobAttributes(jobDataAll);
					generalRequestDTO.setCandidateAttributes(candidateDetails);

					CompletableFuture<EmbeddingListCompareResponseDTO> qualificationFuture = compareEmbeddingsListAsyncMan(
							qualificationRequestDTO, parentContext);
					//					CompletableFuture<EmbeddingListCompareResponseDTO> languageFuture = compareEmbeddingsListAsyncMan(
					//							languageRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> jobTitlesFuture = compareEmbeddingsListAsyncMan(
							jobTitlesRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> jobSkillsFuture = compareEmbeddingsListTextAsyncMan(
							jobSkillsRequestDTO, parentContext);
					CompletableFuture<EmbeddingListCompareResponseDTO> generalFuture = compareEmbeddingsTextAsyncMan(
							generalRequestDTO, parentContext);

					return CompletableFuture.allOf(qualificationFuture, jobTitlesFuture, jobSkillsFuture, generalFuture)
							.thenApply(v -> {
								CandidateMatchingDetailsResponseDTO candidateMatchingDetailsResponseDTO = new CandidateMatchingDetailsResponseDTO();

								// Directly access future results without join(), since we are already in a
								// completion stage.
//								double jobSkillScore = jobSkillsFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double jobTitleScore = jobTitlesFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double qualificationScore = qualificationFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum();
//								double generalScore = generalFuture.join().getSimilar_attributes() != null
//										? generalFuture.join().getSimilar_attributes().stream()
//										.mapToDouble(attribute -> attribute.getScore() == null ? 0.0
//												: attribute.getScore().doubleValue())
//										.sum()
//										: 0.0;
//
//								// Set scores...
//								candidateMatchingDetailsResponseDTO.setSkillsScore(jobSkillScore);
//								candidateMatchingDetailsResponseDTO.setJobTitleScore(jobTitleScore);
//								candidateMatchingDetailsResponseDTO.setQualificationScore(qualificationScore);
//								candidateMatchingDetailsResponseDTO.setGeneralScore(generalScore);

								// Set Normalized Score
								candidateMatchingDetailsResponseDTO.setNormalizedQualificationScore(qualificationFuture.join().getNormalized_score());
								candidateMatchingDetailsResponseDTO.setNormalizedSkillsScore(jobSkillsFuture.join().getNormalized_score());
								candidateMatchingDetailsResponseDTO.setNormalizedJobTitleScore(jobTitlesFuture.join().getNormalized_score());
								candidateMatchingDetailsResponseDTO.setNormalizedGeneralScore(generalFuture.join().getNormalized_score());

								return candidateMatchingDetailsResponseDTO;
							});
					// This .thenCompose is crucial; it flattens the
					// CompletableFuture<CompletableFuture<T>> to CompletableFuture<T>
				}).thenCompose(Function.identity())).collect(Collectors.toList());

		// Wait for all futures to complete and collect the results
		List<CandidateMatchingDetailsResponseDTO> candidateMatchingDetailsResponseDTOList = futures.stream()
				.map(CompletableFuture::join).collect(Collectors.toList());

		// Normalize score between max and min for each section
		// Get the max and min for each section
//		Double maxQualificationScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getQualificationScore).max().orElse(0.0);
//		Double minQualificationScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getQualificationScore).min().orElse(0.0);
//		Double maxSkillsScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getSkillsScore).max().orElse(0.0);
//		Double minSkillsScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getSkillsScore).min().orElse(0.0);
//		Double maxJobTitleScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getJobTitleScore).max().orElse(0.0);
//		Double minJobTitleScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getJobTitleScore).min().orElse(0.0);
//		Double maxGeneralScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getGeneralScore).max().orElse(0.0);
//		Double minGeneralScore = candidateMatchingDetailsResponseDTOList.stream()
//				.mapToDouble(CandidateMatchingDetailsResponseDTO::getGeneralScore).min().orElse(0.0);

		// Normalize the score
		for (CandidateMatchingDetailsResponseDTO ca : candidateMatchingDetailsResponseDTOList) {

//			Double preComputedScore = ca.getNormalizedGeneralScore() * 0.35 + ca.getNormalizedQualificationScore() * 0.05
//					+ ca.getNormalizedSkillsScore() * 0.35 + ca.getNormalizedJobTitleScore() * 0.25;

			double jobTitleScore = ca.getNormalizedJobTitleScore() != null ? ca.getNormalizedJobTitleScore() : 0.0;
			double generalScore = ca.getNormalizedGeneralScore() != null ? ca.getNormalizedGeneralScore() : 0.0;
			double qualificationScore = ca.getNormalizedQualificationScore() != null ? ca.getNormalizedQualificationScore() : 0.0;
			double skillsScore = ca.getNormalizedSkillsScore() != null ? ca.getNormalizedSkillsScore() : 0.0;

			Double preComputedScore = generalScore * 0.35 + qualificationScore * 0.05
					+ skillsScore * 0.35 + jobTitleScore * 0.25;

			ca.setComputedScore(preComputedScore);

		}

		// Update the page content with all these data
		for (int i = 0; i < candidateEntityWithSimilarityList.size(); i++) {
			CandidateEntityWithSimilarity ca = candidateEntityWithSimilarityList.get(i);
			ca.setComputedScore(candidateMatchingDetailsResponseDTOList.get(i).getComputedScore() * 0.5
					+ ca.getSimilarityScore() * 0.5);
		}
		return null;
	}

	@Override
	public CandidateSimilarityListingResponseDTO getCandidateListingPageWithSimilaritySearchAndSearchTerm(
			CandidateListingRequestDTO candidateListingRequestDTO) throws ExecutionException, InterruptedException {
		Integer page = candidateListingRequestDTO.getPage();
		Integer size = candidateListingRequestDTO.getPageSize();
		String sortBy = candidateListingRequestDTO.getSortBy();
		String sortDirection = candidateListingRequestDTO.getSortDirection();
		String searchTerm = candidateListingRequestDTO.getSearchTerm();
		List<String> searchFields = candidateListingRequestDTO.getSearchFields();
		searchFields.add("candidate_complete_info");
		Long jobId = candidateListingRequestDTO.getJobId();
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "cosine_similarity";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		// Get Job Embeddings
		CandidateResponseDTO.HttpResponse jobEmbeddingResponse = jobAPIClient.getEmbeddingsById(jobId, "default");
		EmbeddingResponseDTO jobEmbedding = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
				EmbeddingResponseDTO.class);

		Page<CandidateEntityWithSimilarity> candidateEntityWithSimilarityPage = null;

		Boolean isAdmin = userUtil.checkIsAdmin();
		List<Long> userIds = new ArrayList<>();
		if (!isAdmin) {
			userIds = userUtil.getUsersIdUnderManager();
		}

		try {
			candidateEntityWithSimilarityPage = candidateRepository
					.findAllByOrderByStringWithUserIdsAndSimilaritySearchWithSearchTerm(
							userIds, false, false, true, pageRequest, searchFields,
							searchTerm, jobEmbedding.getEmbedding(), true);
		} catch (Exception e) {
			candidateEntityWithSimilarityPage = candidateRepository
					.findAllByOrderByStringWithUserIdsAndSimilaritySearchWithSearchTerm(
							userIds, false, false, true, pageRequest, searchFields,
							searchTerm, jobEmbedding.getEmbedding(), true);
		}
		return candidateSimilarityPageToCandidateSimilarityListingResponse(candidateEntityWithSimilarityPage, false);
	}

	@Override
	public void updateCandidateEmbeddingsAll() {
		List<CandidateEntity> candidates = candidateRepository.findAllByEmbeddingIsNull();
		if (candidates != null && !candidates.isEmpty()) {
			System.out.println("Total candidates to update: " + candidates.size());
			int count = 0;
			int passedCount = 0;
			int failedCount = 0;
			for (CandidateEntity candidate : candidates) {
				try {
					updateCandidateEmbeddings(candidate.getId());
					passedCount++;
				} catch (Exception e) {
					e.printStackTrace();
					failedCount++;
				}
				count++;
				System.out.println("Updated: " + count + " candidates");
			}
			System.out.println("All candidates updated...");
			System.out.println("Total candidates: " + candidates.size());
			System.out.println("Total passed: " + passedCount);
			System.out.println("Total failed: " + failedCount);
		}

	}

	@Override
	public void softDeleteCandidates(CandidateListingDeleteRequestDTO candidateListingDeleteRequestDTO) {
		if (candidateListingDeleteRequestDTO.getCandidateIds().isEmpty()) {
			throw new RuntimeException("No candidates selected");
		}
		List<CandidateEntity> candidateEntities = candidateRepository
				.findAllByIdsAndDraftAndDeleted(candidateListingDeleteRequestDTO.getCandidateIds(), false, false, true);

		if (candidateEntities.isEmpty()) {
			throw new RuntimeException("No candidates found");
		}

		for (CandidateEntity candidateEntity : candidateEntities) {
			candidateEntity.setDeleted(true);
		}

		candidateRepository.saveAll(candidateEntities);
	}

	private CandidateSimilarityListingResponseDTO candidateSimilarityPageToCandidateSimilarityListingResponse(
			Page<CandidateEntityWithSimilarity> candidateEntityWithSimilarityPage, Boolean toSort) {
		CandidateSimilarityListingResponseDTO candidateSimilarityListingResponseDTO = new CandidateSimilarityListingResponseDTO();
		candidateSimilarityListingResponseDTO.setTotalPages(candidateEntityWithSimilarityPage.getTotalPages());
		candidateSimilarityListingResponseDTO.setTotalElements(candidateEntityWithSimilarityPage.getTotalElements());
		candidateSimilarityListingResponseDTO.setPage(candidateEntityWithSimilarityPage.getNumber());
		candidateSimilarityListingResponseDTO.setPageSize(candidateEntityWithSimilarityPage.getSize());

		if (toSort) {
			// Sort the content based on similarity score
			List<CandidateEntityWithSimilarity> sortedList = candidateEntityWithSimilarityPage.getContent().stream()
					.sorted(Comparator.comparingDouble(CandidateEntityWithSimilarity::getComputedScore).reversed())
					.collect(Collectors.toList());

			candidateSimilarityListingResponseDTO.setCandidates(sortedList);
		} else {
			candidateSimilarityListingResponseDTO.setCandidates(candidateEntityWithSimilarityPage.getContent());
		}

		return candidateSimilarityListingResponseDTO;
	}

	private CompletableFuture<EmbeddingListCompareResponseDTO> compareEmbeddingsListAsyncMan(
			EmbeddingListCompareRequestDTO requestDTO, RequestAttributes context) {
		return AsyncUtil.supplyAsyncWithContextManualAdd(() -> {
			CandidateResponseDTO.HttpResponse response = embeddingAPIClient.compareEmbeddingsList(requestDTO);
			return MappingUtil.mapClientBodyToClass(response.getData(), EmbeddingListCompareResponseDTO.class);
		}, context);
	}

	private CompletableFuture<EmbeddingListCompareResponseDTO> compareEmbeddingsListTextAsyncMan(
			EmbeddingListTextCompareRequestDTO requestDTO, RequestAttributes context) {
		return AsyncUtil.supplyAsyncWithContextManualAdd(() -> {
			CandidateResponseDTO.HttpResponse response = embeddingAPIClient.compareEmbeddingsListText(requestDTO);
			return MappingUtil.mapClientBodyToClass(response.getData(), EmbeddingListCompareResponseDTO.class);
		}, context);
	}

	private CompletableFuture<EmbeddingListCompareResponseDTO> compareEmbeddingsTextAsyncMan(
			EmbeddingTextCompareRequestDTO requestDTO, RequestAttributes context) {
		return AsyncUtil.supplyAsyncWithContextManualAdd(() -> {
			CandidateResponseDTO.HttpResponse response = embeddingAPIClient.compareEmbeddingsText(requestDTO);
			return MappingUtil.mapClientBodyToClass(response.getData(), EmbeddingListCompareResponseDTO.class);
		}, context);
	}

	@Override
	public List<CustomFieldsEntity> getAllCreatedCustomViews() {
		List<CustomFieldsEntity> customfields = candidateCustomFieldsRepository.findAllByUser(getUserId(), "Candidate",
				false);
		return customfields;
	}

	@Override
	public CustomFieldsResponseDTO updateCustomView(Long id) {
		if (candidateCustomFieldsRepository.findById(id).get().getIsDeleted()) {
			throw new DuplicateResourceException(
					messageSource.getMessage("error.customViewAlreadyDeleted", null, LocaleContextHolder.getLocale()));
		}
		List<CustomFieldsEntity> selectedCustomView = candidateCustomFieldsRepository.findAllByUser(getUserId(),
				"Candidate", false);
		for (CustomFieldsEntity customView : selectedCustomView) {
			if (customView.isSelected() == true) {
				customView.setSelected(false);
				candidateCustomFieldsRepository.save(customView);
			}
		}
		Optional<CustomFieldsEntity> customFieldsEntity = candidateCustomFieldsRepository.findById(id);
		customFieldsEntity.get().setSelected(true);
		candidateCustomFieldsRepository.save(customFieldsEntity.get());

		return customFieldsEntityToCustomFieldsResponseDTO(customFieldsEntity.get());

	}

	@Override
	public CustomFieldsResponseDTO saveCustomFields(CustomFieldsRequestDTO customFieldsRequestDTO) {

		if (candidateCustomFieldsRepository.existsByName(customFieldsRequestDTO.getName())) {
			throw new ServiceException(messageSource.getMessage(MessageConstants.CANDIDATE_CUSTOM_VIEW_NAME_EXIST, null,
					LocaleContextHolder.getLocale()));
		}
		List<CustomFieldsEntity> selectedCustomView = candidateCustomFieldsRepository.findAllByUser(getUserId(),
				"Candidate", false);

		if (selectedCustomView != null) {
			for (CustomFieldsEntity customView : selectedCustomView) {
				if (customView.isSelected() == true) {
					customView.setSelected(false);
					candidateCustomFieldsRepository.save(customView);
				}
			}

		}

		System.out.println(" Save candidate customFields : Service");
		System.out.println(customFieldsRequestDTO);
		CustomFieldsEntity candidateCustomFieldsEntity = customFieldsRequestDTOToCustomFieldsEntity(
				customFieldsRequestDTO);
		return customFieldsEntityToCustomFieldsResponseDTO(candidateCustomFieldsEntity);
	}

	CustomFieldsEntity customFieldsRequestDTOToCustomFieldsEntity(CustomFieldsRequestDTO customFieldsRequestDTO) {
		CustomFieldsEntity customFieldsEntity = new CustomFieldsEntity();
		customFieldsEntity.setName(customFieldsRequestDTO.getName());
		customFieldsEntity.setType(customFieldsRequestDTO.getType());
		// converting list of string to comma saparated string
		String columnNames = String.join(",", customFieldsRequestDTO.getColumnName());
		customFieldsEntity.setColumnName(columnNames);
		// customFieldsEntity.setColumnName(customFieldsRequestDTO.getColumnName());
		customFieldsEntity.setCreatedBy(getUserId());
		customFieldsEntity.setUpdatedBy(getUserId());
		customFieldsEntity.setSelected(true);
		return candidateCustomFieldsRepository.save(customFieldsEntity);
	}

	CustomFieldsResponseDTO customFieldsEntityToCustomFieldsResponseDTO(
			CustomFieldsEntity candidateCustomFieldsEntity) {
		CustomFieldsResponseDTO customFieldsResponseDTO = new CustomFieldsResponseDTO();
		// Converting String to List of String.
		String columnNames = candidateCustomFieldsEntity.getColumnName();
		List<String> columnNamesList = Arrays.asList(columnNames.split("\\s*,\\s*"));
		customFieldsResponseDTO.setColumnName(columnNamesList);
		// customFieldsResponseDTO.setColumnName(candidateCustomFieldsEntity.getColumnName());
		customFieldsResponseDTO.setCreatedBy(candidateCustomFieldsEntity.getCreatedBy());
		customFieldsResponseDTO.setName(candidateCustomFieldsEntity.getName());
		customFieldsResponseDTO.setType(candidateCustomFieldsEntity.getType());
		customFieldsResponseDTO.setUpdatedBy(candidateCustomFieldsEntity.getUpdatedBy());
		customFieldsResponseDTO.setId(candidateCustomFieldsEntity.getId());
		return customFieldsResponseDTO;
	}

	@Override
	public void softDelete(Long id) {
		CustomFieldsEntity customFieldsEntity = candidateCustomFieldsRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Custom view not found"));

		// Soft delete the custom view
		customFieldsEntity.setIsDeleted(true);
		customFieldsEntity.setSelected(false);

		// Save custom view
		candidateCustomFieldsRepository.save(customFieldsEntity);
	}

	private String getEmailFromRequest(CandidateRequestDTO candidateRequestDTO) {
		return candidateRequestDTO.getEmail();
	};

	// @Override
//	public List<CandidateNewEntity> getAllCandidatesWithSearch(String query) {
//		List<CandidateNewEntity>candidateEntities = candidateNewRepository.getAllCandidatesWithSearch(query, getUserId(), false, false);
//		return candidateEntities;
//	}

}
