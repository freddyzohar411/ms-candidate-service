package com.avensys.rts.candidate.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.avensys.rts.candidate.payloadnewrequest.CandidateJobSimilaritySearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.avensys.rts.candidate.entity.CandidateEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

public class CustomCandidateRepositoryImpl implements CustomCandidateRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<CandidateEntity> findAllByOrderBy(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
//            orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
			orderByClause = String.format("CAST(%s->>'%s' AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM candidate_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByString(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM candidate_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);

	}

	@Override
	public Page<CandidateEntity> findAllByOrderByNumeric(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Log the generated SQL (for debugging)
		System.out.println(queryString);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM candidate_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchString(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
//		                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchNumeric(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
//		                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByStringWithUserGroups(Set<Long> userGroupIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with userGroups filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("userGroupIds", userGroupIds);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM candidate_new WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userGroupIds", userGroupIds);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByNumericWithUserGroups(Set<Long> userGroupIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with userGroups filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("userGroupIds", userGroupIds);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM candidate_new WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userGroupIds", userGroupIds);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchStringWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable, List<String> searchFields,
			String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("userGroupIds", userGroupIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate_new WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userGroupIds", userGroupIds);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchNumericWithUserGroups(Set<Long> userGroupIds,
			Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable, List<String> searchFields,
			String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate_new WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		query.setParameter("userGroupIds", userGroupIds);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate_new WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND created_by_user_groups_id IS NOT NULL AND (SELECT COUNT(*) FROM UNNEST(string_to_array(created_by_user_groups_id, ',')) AS grp WHERE CAST(grp AS bigint) IN (:userGroupIds)) > 0 AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userGroupIds", userGroupIds);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable) {

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = " AND created_by IN (:userIds)";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM candidate WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				userCondition, orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive %s",
				userCondition);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = " AND created_by IN (:userIds)";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM candidate WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive %s",
				userCondition);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = " AND created_by IN (:userIds)";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive %s AND (%s) ORDER BY %s %s NULLS LAST",
				userCondition, searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive %s AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<CandidateEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = " AND created_by IN (:userIds)";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM candidate WHERE is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive %s AND (%s) ORDER BY %s %s NULLS LAST",
				userCondition, searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, CandidateEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isDraft", isDraft);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<CandidateEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM candidate WHERE is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive %s AND (%s)",
				userCondition, searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public void insertVector(Long candidateId, String columnName, List<Float> vector) {
		// Convert your List<Float> to the format expected by the database for the vector type
		String vectorString = vector.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")); // Note the change to square brackets

		// Prepare your SQL query, ensuring the casting and formatting align with your database's requirements
		String sql = "INSERT INTO candidate (id, :columnName) VALUES (:id, CAST(:vectorText AS vector))"; // Adjust as necessary

		// Execute the native query with parameters, ensuring the correct format is applied
		entityManager.createNativeQuery(sql)
				.setParameter("id", candidateId)
				.setParameter("columnName", columnName)
				.setParameter("vectorText", vectorString) // The vector is now correctly formatted
				.executeUpdate();
	}

	@Override
	@Transactional
	public void updateVector(Long candidateId, String columnName, List<Float> vector) {
		// Convert your List<Float> to the format expected by the database for the vector type
		String vectorString = vector.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")); // Note the change to square brackets

		// Prepare your SQL query, ensuring the casting and formatting align with your database's requirements
		// Note the change to the SQL statement for updating instead of inserting
		String sql = "UPDATE candidate SET " + columnName + " = CAST(:vectorText AS vector) WHERE id = :id";

		// Execute the native query with parameters, ensuring the correct format is applied
		entityManager.createNativeQuery(sql)
				.setParameter("id", candidateId)
				.setParameter("vectorText", vectorString) // The vector is now correctly formatted
				.executeUpdate();
	}

	@Override
	public List<CandidateJobSimilaritySearchResponseDTO> findSimilarEmbeddingsCosine(List<Float> targetVector, String columnName) {
		// Convert List<Float> to the format expected by PostgreSQL's vector type
		String vectorString = targetVector.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",", "[", "]"));

		// Prepare and execute the native SQL query
		String sql = "SELECT id, 1 - (CAST(:queryVector AS vector) <=> " + columnName + ") AS cosine_similarity " +
				"FROM candidate " +
				"WHERE " + columnName + " IS NOT NULL " +  // This line ensures the embedding column is not null
				"ORDER BY cosine_similarity DESC LIMIT 10";

		List<Object[]> resultObjects = entityManager.createNativeQuery(sql)
				.setParameter("queryVector", vectorString)
				.getResultList();

		List<CandidateJobSimilaritySearchResponseDTO> results = new ArrayList<>();
		for (Object[] result : resultObjects) {
			Long candidateId = ((Number) result[0]).longValue();
			Double similarityScore = (Double) result[1];

			CandidateEntity candidate = entityManager.find(CandidateEntity.class, candidateId);
			results.add(new CandidateJobSimilaritySearchResponseDTO(candidate, similarityScore));
		}

		return results;
	}

}
