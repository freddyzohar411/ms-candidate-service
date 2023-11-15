package com.avensys.rts.candidate.util;

public class StringUtil {
	public static String convertCamelCaseToTitleCase(String camelCaseString) {
		StringBuilder result = new StringBuilder();

		for (char c : camelCaseString.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append(' ');
			}
			result.append(Character.toLowerCase(c));
		}

		result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
		return result.toString();
	}

	public static String convertCamelCaseToTitleCase2(String camelCaseString) {
		StringBuilder result = new StringBuilder();

		for (char c : camelCaseString.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append(' ');
			}
			result.append(c);
		}

		String[] words = result.toString().split(" ");
		result.setLength(0);

		for (String word : words) {
			if (!word.isEmpty()) {
				result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
			}
		}

		return result.toString().trim();
	}

	public static String camelCaseToSnakeCase(String input) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);

			// If it's an uppercase letter, add an underscore before it
			if (Character.isUpperCase(currentChar)) {
				if (i > 0) {
					result.append("_");
				}
				result.append(Character.toLowerCase(currentChar));
			} else {
				result.append(currentChar);
			}
		}
		return result.toString();
	}

}
