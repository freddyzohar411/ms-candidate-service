package com.avensys.rts.candidate.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.avensys.rts.candidate.annotation.RequiresAllPermissions;
import com.avensys.rts.candidate.annotation.RequiresAnyPermission;
import com.avensys.rts.candidate.constant.MessageConstants;
import com.avensys.rts.candidate.enums.Permission;
import com.avensys.rts.candidate.exception.PermissionDeniedException;
import com.avensys.rts.candidate.util.UserUtil;

/**
 * Author: Koh He Xiang Aspect to check if the user has the required permission
 * (Both all and any)
 */
@Aspect
@Component
public class SecurityPermissionAspect {

	private final Logger log = LoggerFactory.getLogger(SecurityPermissionAspect.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserUtil userUtil;

	@Before("@annotation(requiresPermission)")
	public void checkAllPermission(RequiresAllPermissions requiresPermission) {
		System.out.println("RequiresAllPermissions Aspect");
		List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();
		if (!requiredPermissions.isEmpty()) {
			// Logic to get permission from UserAPI Microservice
			Map<String, Set<String>> modulePermissions = userUtil.getModulePermissions();
			if (modulePermissions != null) {
				requiredPermissions.forEach(modulePermission -> {
					String[] modulePermissionArray = modulePermission.split(":");
					if (!userUtil.checkAPermissionWithModule(modulePermissions, modulePermissionArray[0],
							modulePermissionArray[1])) {
						throw new PermissionDeniedException(
								messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null,
										LocaleContextHolder.getLocale()));
					}
				});
			} else {
				throw new PermissionDeniedException(messageSource.getMessage(
						MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
			}
		}
	}

	/**
	 * Check if the user has at least one permission from the required permissions
	 * @param requiresPermission
	 */
	@Before("@annotation(requiresPermission)")
	public void checkAnyPermission(RequiresAnyPermission requiresPermission) {
		System.out.println("RequiresAnyPermissions Aspect");
		List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();

		if (!requiredPermissions.isEmpty()) {
			// Logic to get permission from UserAPI Microservice
			Map<String, Set<String>> modulePermissions = userUtil.getModulePermissions();
			// Check if one permission meet
			if (modulePermissions != null) {
				requiredPermissions.forEach(modulePermission -> {
					String[] modulePermissionArray = modulePermission.split(":");
					if (userUtil.checkAPermissionWithModule(modulePermissions, modulePermissionArray[0],
							modulePermissionArray[1])) {
						return;
					}
				});
				throw new PermissionDeniedException(messageSource.getMessage(
						MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
			} else {
				throw new PermissionDeniedException(messageSource.getMessage(
						MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
			}
		}
	}

}
