package com.avensys.rts.candidate.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.avensys.rts.candidate.payloadnewrequest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.candidate.annotation.RequiresAllPermissions;
import com.avensys.rts.candidate.constant.MessageConstants;
import com.avensys.rts.candidate.entity.CustomFieldsEntity;
import com.avensys.rts.candidate.enums.Permission;
import com.avensys.rts.candidate.payloadnewresponse.CandidateMappingResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.CustomFieldsResponseDTO;
import com.avensys.rts.candidate.service.CandidateMappingService;
import com.avensys.rts.candidate.service.CandidateServiceImpl;
import com.avensys.rts.candidate.util.ResponseUtil;
import com.avensys.rts.candidate.util.UserUtil;

import jakarta.validation.Valid;

/**
 * Author:Kotaiah nalleboina This is the New Controller class for candidate that
 * work with dynamic forms.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/candidates")
public class CandidateController {

	private final Logger LOG = LoggerFactory.getLogger(CandidateController.class);

	@Autowired
	private CandidateServiceImpl candidateNewService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CandidateMappingService candidateMappingService;

	@Autowired
	private UserUtil userUtil;

	public CandidateController(CandidateServiceImpl candidateNewService, MessageSource messageSource) {
		this.candidateNewService = candidateNewService;
		this.messageSource = messageSource;
	}

	/**
	 * This method is used to create Candidate draft
	 * 
	 * @param candidateRequestDTO
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_WRITE })
	@PostMapping("/add")
	public ResponseEntity<Object> addCandidate(@Valid @ModelAttribute CandidateRequestDTO candidateRequestDTO) {
		LOG.info("Candidate create: Controller");
		System.out.println("CandidateNewController: " + candidateRequestDTO);
		CandidateResponseDTO candidateResponseDTO = candidateNewService.createCandidate(candidateRequestDTO);
		return ResponseUtil.generateSuccessResponse(candidateResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.CANDIDATE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This method is used to retrieve Candidate Data by id
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_READ })
	@GetMapping("/{id}")
	public ResponseEntity<Object> getCandidate(@PathVariable int id) {
		LOG.info("Candidate get: Controller");
		CandidateResponseDTO candidateResponseDTO = candidateNewService.getCandidate(id);
		return ResponseUtil.generateSuccessResponse(candidateResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This method is used to update Candidate Data by id
	 * 
	 * @param id
	 * @param candidateRequestDTO
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_EDIT })
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateCandidate(@PathVariable int id,
			@ModelAttribute CandidateRequestDTO candidateRequestDTO) {
		LOG.info("Candidate update: Controller");
		CandidateResponseDTO candidateResponseDTO = candidateNewService.updateCandidate(id, candidateRequestDTO);
		return ResponseUtil.generateSuccessResponse(candidateResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_UPDATED, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This method is used to delete draft Candidate (Hard Delete)
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_DELETE })
	@DeleteMapping("/draft/{id}")
	public ResponseEntity<Object> deleteCandidate(@PathVariable int id) {
		LOG.info("Candidate delete: Controller");
		candidateNewService.deleteDraftCandidate(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_DELETED, null, LocaleContextHolder.getLocale()));

	}

	/**
	 * Complet Candidate creation
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_WRITE })
	@PutMapping("/{id}/complete")
	public ResponseEntity<Object> completeCandidateCreate(@PathVariable int id) {
		LOG.info("Candidate complete: Controller");
		CandidateResponseDTO candidateResponseDTO = candidateNewService.completeCandidateCreate(id);
		return ResponseUtil.generateSuccessResponse(candidateResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get candidate draft if exists
	 * 
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_WRITE })
	@GetMapping("/draft")
	public ResponseEntity<Object> getCandidateIfDraft() {
		LOG.info("Candidate get: Controller");
		CandidateResponseDTO candidateResponseDTO = candidateNewService.getCandidateIfDraft();
		return ResponseUtil.generateSuccessResponse(candidateResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Soft delete existing candidate
	 * 
	 * @param id
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_DELETE })
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> softDeleteCandidate(@PathVariable int id) {
		LOG.info("Candidate soft delete: Controller");
		candidateNewService.softDeleteCandidate(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_DELETED, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get all candidate field for all forms related to candidates
	 * 
	 * @return
	 */
	@RequiresAllPermissions({ Permission.CANDIDATE_READ })
	@GetMapping("/fields")
	public ResponseEntity<Object> getAllCandidatesFields() {
		LOG.info("Candidate get all fields: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.getAllCandidatesFields(), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@RequiresAllPermissions({ Permission.CANDIDATE_READ })
	@PostMapping("/listing/similarity-search")
	public ResponseEntity<Object> getCandidateListingMatch(
			@RequestBody CandidateListingRequestDTO candidateListingRequestDTO)
			throws ExecutionException, InterruptedException {
		LOG.info("Candidate get all fields: Controller");
		String searchTerm = candidateListingRequestDTO.getSearchTerm();
		if (searchTerm == null || searchTerm.isEmpty()) {
			try {
				return ResponseUtil.generateSuccessResponse(
						candidateNewService.getCandidateListingPageWithSimilaritySearch(candidateListingRequestDTO),
						HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null,
								LocaleContextHolder.getLocale()));
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return ResponseUtil.generateSuccessResponse(
				candidateNewService.getCandidateListingPageWithSimilaritySearchAndSearchTerm(
						candidateListingRequestDTO),
				HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@RequiresAllPermissions({ Permission.CANDIDATE_READ })
	@PostMapping("/listing/all")
	public ResponseEntity<Object> getCandidateListingAll(
			@RequestBody CandidateListingRequestDTO candidateListingRequestDTO) {
		LOG.info("Candidate get all candidate listing: Controller");
		Integer page = candidateListingRequestDTO.getPage();
		Integer pageSize = candidateListingRequestDTO.getPageSize();
		String sortBy = candidateListingRequestDTO.getSortBy();
		String sortDirection = candidateListingRequestDTO.getSortDirection();
		String searchTerm = candidateListingRequestDTO.getSearchTerm();
		List<String> searchFields = candidateListingRequestDTO.getSearchFields();
		Long jobId = candidateListingRequestDTO.getJobId();
		Boolean isAdmin = candidateListingRequestDTO.getAllActive() ? candidateListingRequestDTO.getAllActive()
				: userUtil.checkIsAdmin();
		Boolean isDownload = candidateListingRequestDTO.getIsDownload();
		List<FilterDTO> filters = candidateListingRequestDTO.getFilters();

		if (searchTerm == null || searchTerm.isEmpty()) {
			return ResponseUtil.generateSuccessResponse(
					candidateNewService.getCandidateListingPage(page, pageSize, sortBy, sortDirection, isAdmin, jobId,
							isDownload, filters),
					HttpStatus.OK, messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null,
							LocaleContextHolder.getLocale()));
		}
		return ResponseUtil.generateSuccessResponse(
				candidateNewService.getCandidateListingPageWithSearch(page, pageSize, sortBy, sortDirection, searchTerm,
						searchFields, isAdmin, jobId, isDownload, filters),
				HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get candidate data by id
	 * 
	 * @param candidateId
	 * @return
	 */
	@GetMapping("/{candidateId}/data")
	public ResponseEntity<Object> getCandidateByIdData(@PathVariable Integer candidateId) {
		LOG.info("Account get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.getCandidateByIdData(candidateId),
				HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get all candidate field for all forms related to candidates including all
	 * related microservices
	 * 
	 * @return
	 */
	@GetMapping("/fields/all")
	public ResponseEntity<Object> getAllCandidatesFieldsAll() {
		LOG.info("Candidate get all fields: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.getAllCandidatesFieldsAll(), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get candidate data by id including all related microservices
	 * 
	 * @param candidateId
	 * @return
	 */
	@GetMapping("/{candidateId}/data/all")
	public ResponseEntity<Object> getCandidateByIdDataAll(@PathVariable Integer candidateId) {
		LOG.info("Account get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.getCandidateByIdDataAll(candidateId),
				HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Save Candidate Mapping
	 *
	 */
	@PostMapping("/mapping/save")
	public ResponseEntity<Object> saveCandidateMapping(
			@RequestBody CandidateMappingRequestDTO candidateListingDataDTO) {
		LOG.info("Candidate save mapping: Controller");
		CandidateMappingResponseDTO candidateMappingResponseDTO = candidateMappingService
				.saveCandidateMapping(candidateListingDataDTO);
		return ResponseUtil.generateSuccessResponse(candidateMappingResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.CANDIDATE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get Candidate Mapping
	 */
	@GetMapping("/mapping/get")
	public ResponseEntity<Object> getCandidateMapping() {
		LOG.info("Candidate get mapping: Controller");
		CandidateMappingResponseDTO candidateMappingResponseDTO = candidateMappingService.getCandidateMapping();
		return ResponseUtil.generateSuccessResponse(candidateMappingResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.CANDIDATE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("create-embeddings/{candidateId}")
	public ResponseEntity<Object> createCandidateEmbeddings(@PathVariable Integer candidateId) {
		LOG.info("Candidate create embeddings: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.updateCandidateEmbeddings(candidateId),
				HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("create-embeddings/all")
	public ResponseEntity<Object> createCandidateEmbeddingsAll() {
		LOG.info("Candidate create embeddings: Controller");
		candidateNewService.updateCandidateEmbeddingsAll();
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PostMapping("similarity-search/job")
	public ResponseEntity<Object> similaritySearch(
			@RequestBody CandidateJobSimilaritySearchRequestDTO jobSimilaritySearchRequestDTO) {
		LOG.info("Candidate similarity search: Controller");
		return ResponseUtil.generateSuccessResponse(
				candidateNewService.getCandidateJobSimilaritySearch(jobSimilaritySearchRequestDTO), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("match/candidates/{candidateId}/jobs/{jobId}")
	public ResponseEntity<Object> getMatchCandidateToJobData(@PathVariable Integer candidateId,
			@PathVariable Long jobId) {
		LOG.info("Candidate match to job: Controller");
		CandidateMatchingDetailsResponseDTO candidateMatchingDetailsResponseDTO = candidateNewService
				.getMatchCandidateToJobData(candidateId, jobId);
		return ResponseUtil.generateSuccessResponse(candidateMatchingDetailsResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/*
	 * save all the fields in the custom view
	 */
	@PostMapping("/save/customfields")
	public ResponseEntity<Object> saveCustomFields(@Valid @RequestBody CustomFieldsRequestDTO customFieldsRequestDTO) {
		LOG.info("Save Candidate customFields: Controller");
		CustomFieldsResponseDTO customFieldsResponseDTO = candidateNewService.saveCustomFields(customFieldsRequestDTO);
		return ResponseUtil.generateSuccessResponse(customFieldsResponseDTO, HttpStatus.CREATED, messageSource
				.getMessage(MessageConstants.CANDIDATE_CUSTOM_VIEW, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/customView/all")
	public ResponseEntity<Object> getAllCreatedCustomViews() {
		LOG.info("Candidate get all custom views: Controller");
		List<CustomFieldsEntity> customViews = candidateNewService.getAllCreatedCustomViews();
		return ResponseUtil.generateSuccessResponse(customViews, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/customView/update/{id}")
	public ResponseEntity<Object> updateCustomView(@PathVariable Long id) {
		LOG.info("Candidate custom view update: Controller");
		CustomFieldsResponseDTO response = candidateNewService.updateCustomView(id);
		return ResponseUtil.generateSuccessResponse(response, HttpStatus.OK, messageSource
				.getMessage(MessageConstants.CANDIDATE_CUSTOM_VIEW_UPDATED, null, LocaleContextHolder.getLocale()));
	}

	@DeleteMapping("/customView/delete/{id}")
	public ResponseEntity<Object> softDeleteCustomView(@PathVariable Long id) {
		LOG.info("Custom view soft delete: Controller");
		candidateNewService.softDelete(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource
				.getMessage(MessageConstants.CANDIDATE_CUSTOM_VIEW_DELETED, null, LocaleContextHolder.getLocale()));
	}

	@PostMapping("/listing/delete")
	public ResponseEntity<Object> deleteCandidateListing(
			@RequestBody CandidateListingDeleteRequestDTO candidateListingDeleteRequestDTO) {
		LOG.info("Candidate listing delete: Controller");
		candidateNewService.softDeleteCandidates(candidateListingDeleteRequestDTO);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/customView/{customViewId}")
	public ResponseEntity<Object> getCandidateCustomView(@PathVariable Long customViewId) {
		LOG.info("Account get custom view: Controller");
		return ResponseUtil.generateSuccessResponse(candidateNewService.getCustomFieldsById(customViewId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PostMapping("/customView/edit/{customViewId}")
	public ResponseEntity<Object> editCandidateCustomView(@PathVariable Long customViewId,
			@RequestBody CustomFieldsRequestDTO customFieldsRequestDTO) {
		LOG.info("Account edit custom view: Controller");
		CustomFieldsResponseDTO customFieldsResponseDTO = candidateNewService.editCustomFieldsById(customViewId,
				customFieldsRequestDTO);
		return ResponseUtil.generateSuccessResponse(customFieldsResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

}
