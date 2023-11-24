package com.avensys.rts.candidate.bootstrap;

import com.avensys.rts.candidate.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.user.UserGroupResponseDTO;
import com.avensys.rts.candidate.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Configuration
@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

	@Autowired
	UserUtil userUtil;

	@Override
	public void run(String... args) throws Exception {
		// Create sample data for testing
		UserGroupResponseDTO group1 = new UserGroupResponseDTO(1L, "Group 1", "Description 1", null, null);
		UserGroupResponseDTO group2 = new UserGroupResponseDTO(2L, "Group 2", "Description 2", null, group1);
		UserGroupResponseDTO group3 = new UserGroupResponseDTO(3L, "Group 3", "Description 3", null, group2);

		UserGroupResponseDTO group4 = new UserGroupResponseDTO(4L, "Group 4", "Description 4", null, group3);
		UserGroupResponseDTO group5 = new UserGroupResponseDTO(5L, "Group 5", "Description 5", null, group4);

		UserGroupResponseDTO group6 = new UserGroupResponseDTO(6L, "Group 6", "Description 6", null, null);

		UserDetailsResponseDTO userDetails = new UserDetailsResponseDTO(
				101L, "key123", "John", "Doe", "john.doe", "john.doe@example.com", "123456789", "E123", false, true,
//				List.of(group3, group5, group6)
				List.of(group4)
		);

		// Test the recursive function
		Set<Long> result = userUtil.mapUserDetailsToUserGroupIdsWithChildren(userDetails);
		String parentResult = userUtil.getUserGroupIdsAsString(userDetails);

		// Parent result
		System.out.println("User Group IDs (Parent): " + parentResult);
		// Print the result
		System.out.println("User Group IDs: " + result);


	}
}
