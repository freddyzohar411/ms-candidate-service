package com.avensys.rts.candidate.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JobDataExtractionUtil {

	public static void printJSON(JsonNode jobData) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			System.out.println(mapper.writeValueAsString(jobData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String extractJobInfo(JsonNode jobData) {
		StringBuilder details = new StringBuilder();
		JsonNode jobInfo = jobData.get("jobInfo");
		if (jobInfo != null) { // Check if basicInfo is not null

			// Basic Information
			if (jobInfo.has("jobTitle")) {
				String jobTitle = jobInfo.get("jobTitle").asText("");
				if (!jobTitle.isEmpty()) {
					details.append("Job Title: ").append(jobTitle).append("\n");
				}
			}

			// Job Country
			if (jobInfo.has("jobCountry")) {
				String jobCountry = jobInfo.get("jobCountry").asText("");
				if (!jobCountry.isEmpty()) {
					details.append("Job Country: ").append(jobCountry).append("\n");
				}
			}

			// Work Location
			if (jobInfo.has("country")) {
				String country = jobInfo.get("country").asText("");
				if (!country.isEmpty()) {
					details.append("Country: ").append(country).append("\n");
				}
			}

			// Job Type
			if (jobInfo.has("jobType")) {
				String jobType = jobInfo.get("jobType").asText("");
				if (!jobType.isEmpty()) {
					details.append("Job Type: ").append(jobType).append("\n");
				}
			}

			// Qualification
			if (jobInfo.has("qualification")) {
				String qualification = jobInfo.get("qualification").asText("");
				if (!qualification.isEmpty()) {
					details.append("Qualification: ").append(qualification).append("\n");
				}
			}

			// Languages is only a text
			if (jobInfo.has("languages")) {
				String languages = jobInfo.get("languages").asText("");
				if (!languages.isEmpty()) {
					details.append("Languages requirement for the job: ").append(languages).append("\n");
				}
			}

			// Job Description
			if (jobInfo.has("Jobdescription")) {
				String jobDescription = jobInfo.get("Jobdescription").asText("");
				if (!jobDescription.isEmpty()) {
					details.append("Job Description: ").append(jobDescription).append("\n");
				}
			}

		}
		return details.toString();
	}

}
