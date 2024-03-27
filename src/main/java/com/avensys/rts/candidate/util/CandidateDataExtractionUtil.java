package com.avensys.rts.candidate.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CandidateDataExtractionUtil {

	public static void printJSON(JsonNode candidateData) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			System.out.println(mapper.writeValueAsString(candidateData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String extractBasicInfoDetailsIncludingSkills(JsonNode candidate) {
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

	public static String extractEducationDetails(JsonNode candidate) {
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

	public static String extractWorkExperienceDetails(JsonNode candidate) {
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
	public static String extractAllDetails(JsonNode candidate) {
		StringBuilder details = new StringBuilder();
		details.append(extractBasicInfoDetailsIncludingSkills(candidate));
		details.append(extractEducationDetails(candidate));
		details.append(extractWorkExperienceDetails(candidate));
		return details.toString();
	}

	// Extract the candidate skills in a Set<String> format
	public static HashSet<String> extractCandidateSkillsSet(JsonNode candidate) {
		HashSet<String> skillsSet = new HashSet<>();
		JsonNode basicInfo = candidate.get("basicInfo");
		if (basicInfo != null) { // Check if basicInfo is not null

			List<String> skillKeys = Arrays.asList("primarySkill", "primarySkills", "skill1", "skill2", "skill3",
					"secondarySkill", "secondarySkills");
			for (String key : skillKeys) {
				if (basicInfo.has(key) && !basicInfo.get(key).asText().isEmpty()) {
					// Check if it is comma seperated. If it is split and add to the set. Else just
					// add it to the set
					if (basicInfo.get(key).asText().contains(",")) {
						String[] skills = basicInfo.get(key).asText().split(",");
						for (String skill : skills) {
							skillsSet.add(skill.trim());
						}
					} else {
						skillsSet.add(basicInfo.get(key).asText().trim());
					}
				}
			}
		}
		return skillsSet;
	}

//	public static HashSet<String> extractCandidateLanguagesSet(JsonNode candidate) {
//		HashSet<String> languagesSet = new HashSet<>();
//		JsonNode languages = candidate.get("languages");
//		if (languages != null && languages.isArray() && languages.size() > 0) {
//			for (JsonNode languageNode : languages) {
//				if (languageNode.has("language") && !languageNode.get("language").asText().isEmpty()) {
//					languagesSet.add(languageNode.get("language").asText());
//				}
//			}
//		}
//		return languagesSet;
//	}

	public static HashSet<String> extractCandidateLanguagesSet(JsonNode candidate) {
		// Add Synonyms for languages
		Map<String, String> languageSynonyms = new HashMap<>();
		languageSynonyms.put("mandarin", "chinese");
		languageSynonyms.put("chinese", "mandarin");

		HashSet<String> languagesSet = new HashSet<>();
		JsonNode languages = candidate.get("languages");
		if (languages != null && languages.isArray() && languages.size() > 0) {
			for (JsonNode languageNode : languages) {
				if (languageNode.has("language") && !languageNode.get("language").asText().isEmpty()) {
					String language = languageNode.get("language").asText();
					// Check if the language is a synonym, if yes, get its main language
					if (languageSynonyms.containsKey(language)) {
						languagesSet.add(languageSynonyms.get(language));
					} else {
						languagesSet.add(language);
					}
				}
			}
		}
		if (languagesSet.isEmpty()) {
			// Add a default language if no languages are found
			languagesSet.add("english");
		}
		return languagesSet;
	}

	public static HashSet<String> extractCandidateWorkTitlesSet(JsonNode candidate) {
		HashSet<String> workTitlesSet = new HashSet<>();
		JsonNode workExperiences = candidate.get("workExperiences");
		if (workExperiences != null && workExperiences.isArray()) { // Check if workExperiences is not null and is an
			// array
			for (JsonNode item : workExperiences) {
				String title = item.has("title") ? item.get("title").asText("") : "";
				if (!title.isEmpty()) {
					workTitlesSet.add(title);
				}
			}
		}
		return workTitlesSet;
	}

	public static HashSet<String> extractCandidateEducationQualificationsSet(JsonNode candidate) {
		HashSet<String> qualificationsSet = new HashSet<>();
		JsonNode educationDetails = candidate.get("educationDetails");
		if (educationDetails != null && educationDetails.isArray()) {
			for (JsonNode item : educationDetails) {
				String qualification = item.has("qualification") ? item.get("qualification").asText("") : "";
				if (!qualification.isEmpty()) {
					qualificationsSet.add(qualification);
				}
			}
		}

//		// Define synonyms for each qualification type
//		Map<String, Set<String>> qualificationSynonyms = new HashMap<>();
//		qualificationSynonyms.put("bachelor", new HashSet<>(Arrays.asList("BSc", "BA", "BS", "BEng", "BTech")));
//		qualificationSynonyms.put("master", new HashSet<>(Arrays.asList("MSc", "MA", "MS", "MEng", "MTech")));
//		qualificationSynonyms.put("diploma", new HashSet<>(Arrays.asList("Dip")));
//		qualificationSynonyms.put("phd", new HashSet<>(Arrays.asList("Ph.D", "Doctorate")));
//		qualificationSynonyms.put("doctorate", new HashSet<>(Arrays.asList("Ph.D", "Doctorate")));
//		qualificationSynonyms.put("certification", new HashSet<>(Arrays.asList("Cert")));
//		// Add more mappings as necessary
//
//		// Initialize a new set to store qualification synonyms
//		HashSet<String> qualificationSynonymsSet = new HashSet<>();
//
//		// Generate synonyms for each found qualification and add them to the set
//		for (String qualification : qualificationsSet) {
//			for (String type : qualificationSynonyms.keySet()) {
//				if (qualification.toLowerCase().contains(type)) {
//					// Add all synonyms of the type
//					for (String synonym : qualificationSynonyms.get(type)) {
//						// Replace the original type in the qualification with its synonym
//						String modifiedQualification = qualification.replaceFirst("(?i)" + type, synonym);
//						qualificationSynonymsSet.add(modifiedQualification);
//					}
//				}
//			}
//		}
//
//		// Add the synonyms to the original qualifications set
//		qualificationsSet.addAll(qualificationSynonymsSet);

		// Extract out qualification types and add them into the set
		HashSet<String> qualificationTypesSet = new HashSet<>();
		Set<String> qualificationTypes = new HashSet<>(
				Arrays.asList("diploma", "bachelor", "master", "phd", "doctorate", "certification", "btech", "mtech"));
		for (String qualification : qualificationsSet) {
			for (String type : qualificationTypes) {
				if (qualification.toLowerCase().contains(type)) {
					qualificationTypesSet.add(type);
				}
			}
		}

		// Add the qualification types to the qualifications set
		qualificationsSet.addAll(qualificationTypesSet);
		return qualificationsSet;
	}

	public static String extractCandidateNationality(JsonNode candidate) {
		JsonNode basicInfo = candidate.get("basicInfo");
		if (basicInfo != null && basicInfo.has("candidateNationality")) {
			return basicInfo.get("candidateNationality").asText();
		}
		return "";
	}

}