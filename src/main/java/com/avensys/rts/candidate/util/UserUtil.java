package com.avensys.rts.candidate.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.candidate.APIClient.UserAPIClient;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.CandidateResponseDTO.HttpResponse;
import com.avensys.rts.candidate.payloadnewresponse.user.ModuleResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.user.RoleResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.user.UserGroupResponseDTO;

@Service
public class UserUtil {

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private JwtUtil jwtUtil;

	public Set<Long> getUserGroupIds(UserDetailsResponseDTO userDetailsResponse) {
		return mapUserDetailsToUserGroupIds(userDetailsResponse);
	}

	public Set<Long> getUserGroupIds() {
		UserDetailsResponseDTO userDetailsResponse = getUserDetails();
		return mapUserDetailsToUserGroupIds(userDetailsResponse);
	}

	public List<Long> getUsersIdUnderManager() {
		CandidateResponseDTO.HttpResponse response = userAPIClient.getUsersUnderManager();
		return (List<Long>) response.getData();
	}

	public String getUserGroupIdsAsString() {
		Set<Long> userGroupIds = getUserGroupIds();
		StringJoiner joiner = new StringJoiner(",");
		for (Long value : userGroupIds) {
			joiner.add(value.toString());
		}
		return joiner.toString();
	}

	public String getUserGroupIdsAsString(UserDetailsResponseDTO userDetailsResponse) {
		Set<Long> userGroupIds = getUserGroupIds(userDetailsResponse);
		StringJoiner joiner = new StringJoiner(",");
		for (Long value : userGroupIds) {
			joiner.add(value.toString());
		}
		return joiner.toString();
	}

	/**
	 * Map the user details to a map of module and permissions
	 * 
	 * @param userDetailsResponse
	 * @return
	 */
	private Map<String, Set<String>> mapUserDetailToUserPermissions(UserDetailsResponseDTO userDetailsResponse) {
		Map<String, Set<String>> modulePermissions = new HashMap<>();
		// Check if Usergroup is null or empty
		List<UserGroupResponseDTO> userGroups = userDetailsResponse.getUserGroup();
		if (userGroups == null || userGroups.isEmpty()) {
			return null;
		}

		for (UserGroupResponseDTO userGroup : userGroups) {
			for (RoleResponseDTO role : userGroup.getRoles()) {
				for (ModuleResponseDTO module : role.getModules()) {
					String moduleName = module.getModuleName();
					Set<String> permissions = new HashSet<>(module.getPermissions());

					// If the module already exists in the map, add to its permissions
					modulePermissions.computeIfAbsent(moduleName, k -> new HashSet<>()).addAll(permissions);
				}
			}
		}
		return modulePermissions;
	}

	/**
	 * Check if the user has any of the permissions specified in the annotation
	 * 
	 * @param modulePermissions
	 * @param requiredPermissions
	 * @return
	 */
	public Boolean checkAPermissionWithModule(Map<String, Set<String>> modulePermissions, String module,
			String permission) {
		if (modulePermissions.containsKey(module)) {
			Set<String> permissions = modulePermissions.get(module);
			if (permissions.contains(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param modulePermissions
	 * @param module
	 * @param requiredPermissions
	 * @return
	 */
	public Boolean checkAllPermissionWithModule(Map<String, Set<String>> modulePermissions, String module,
			List<String> requiredPermissions) {
		if (modulePermissions.containsKey(module)) {
			Set<String> permissions = modulePermissions.get(module);
			if (permissions.containsAll(requiredPermissions)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get User usergroup in a list
	 */
	private Set<Long> mapUserDetailsToUserGroupIds(UserDetailsResponseDTO userDetailsResponse) {
		Set<Long> userGroupIds = new HashSet<>();
		List<UserGroupResponseDTO> userGroups = userDetailsResponse.getUserGroup();
		if (userGroups == null || userGroups.isEmpty()) {
			return null;
		}
		for (UserGroupResponseDTO userGroup : userGroups) {
			userGroupIds.add(userGroup.getId());
		}
		return userGroupIds;
	}

	public UserDetailsResponseDTO getUserDetails() {
		CandidateResponseDTO.HttpResponse userResponse = userAPIClient.getUserDetail();
		UserDetailsResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				UserDetailsResponseDTO.class);
		return userData;
	}

	/**
	 * Get module permission
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getModulePermissions() {
		Map<String, Set<String>> modulePermissions = mapUserDetailToUserPermissions(getUserDetails());
		return modulePermissions;
	}

	public Set<Long> mapUserDetailsToUserGroupIdsWithChildren(UserDetailsResponseDTO userDetailsResponse) {
		Set<Long> userGroupIds = new HashSet<>();
		List<UserGroupResponseDTO> userGroups = userDetailsResponse.getUserGroup();
		if (userGroups == null || userGroups.isEmpty()) {
			return userGroupIds;
		}

		// Recursion
		for (UserGroupResponseDTO userGroup : userGroups) {
			userGroupIds.add(userGroup.getId());
			userGroupIds.addAll(mapUserGroupToUserGroupIdsWithChildren(userGroup));
		}
		return userGroupIds;
	}

	private Set<Long> mapUserGroupToUserGroupIdsWithChildren(UserGroupResponseDTO userGroup) {
		Set<Long> userGroupIds = new HashSet<>();
		userGroupIds.add(userGroup.getId());

		// Recursion
		UserGroupResponseDTO parentUserGroup = userGroup.getParentUserGroup();
		if (parentUserGroup != null) {
			userGroupIds.addAll(mapUserGroupToUserGroupIdsWithChildren(parentUserGroup));
		}

		return userGroupIds;
	}

	public Boolean checkIsAdmin() {
		Boolean flag = false;
		HttpResponse userResponse = userAPIClient.getUserDetail();
		UserDetailsResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				UserDetailsResponseDTO.class);
		if (userData.getUserGroup() != null && userData.getUserGroup().size() > 0) {
			for (UserGroupResponseDTO grp : userData.getUserGroup()) {
				if (grp.getRoles() != null && grp.getRoles().size() > 0) {
					for (RoleResponseDTO role : grp.getRoles()) {
						if (role.getRoleName().toLowerCase().contains("admin")) {
							flag = true;
						}
					}
				}
			}
		}
		return flag;
	}

}
