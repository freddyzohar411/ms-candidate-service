package com.avensys.rts.candidate.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.avensys.rts.candidate.payloadnewrequest.FilterDTO;

public class QueryUtil {

	public final static String EQUAL = "Equal";
	public final static String NOT_EQUAL = "Not Equal";
	public final static String CONTAINS = "Contains";
	public final static String DOES_NOT_CONTAIN = "Does Not Contain";
	public final static String STARTS_WITH = "Starts With";
	public final static String ENDS_WITH = "Ends With";
	public final static String GREATER_THAN = "Greater Than";
	public final static String LESS_THAN = "Less Than";
	public final static String IS_EMPTY = "Is Empty";
	public final static String IS_NOT_EMPTY = "Is Not Empty";
	public final static String IS_TRUE = "Is True";
	public final static String IS_FALSE = "Is False";
	public final static String IS_NULL = "Is Null";
	public final static String IS_NOT_NULL = "Is Not Null";

	public final static String BEFORE = "Before";
	public final static String AFTER = "After";

	public final static String IN = "In";

	public final static String NOT_IN = "Not In";

	public static String buildQueryFromFilters(List<FilterDTO> filters) {
		if (filters == null || filters.isEmpty())
			return "";

		StringBuilder currentGroup = new StringBuilder();
		int parameterPosition = 1;

		for (int i = 0; i < filters.size(); i++) {
			FilterDTO filter = filters.get(i);
			String column = filter.getField();
			String condition = filter.getCondition();
			String value = filter.getValue();
			String operator = filter.getOperator();
			System.out.println("column: " + column);
			System.out.println("condition: " + condition);
			System.out.println("value: " + value);
			System.out.println("operator: " + operator);

			String conditionString = buildConditionString(column, condition, value, parameterPosition);

			if (i == 0) {
				currentGroup.append(conditionString);
			} else {
				currentGroup = new StringBuilder("(" + currentGroup + " " + operator + " " + conditionString + ")");
			}
			parameterPosition++;
		}

		return currentGroup.toString();
	}

	private static String buildConditionString(String column, String condition, String value, int parameterPosition) {
		StringBuilder conditionString = new StringBuilder();
//		String sqlOperator = getSqlCondition(condition);

		if (column.contains(".")) {
			String[] parts = column.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			switch (condition) {
			case EQUAL:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%s'", jsonColumnName, jsonKey, value));
				break;
			case NOT_EQUAL:
				conditionString.append(String.format("(%s->>'%s') NOT ILIKE '%s'", jsonColumnName, jsonKey, value));
				break;
			case CONTAINS:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%%%s%%'", jsonColumnName, jsonKey, value));
				break;
			case DOES_NOT_CONTAIN:
				conditionString.append(String.format("(%s->>'%s') NOT ILIKE '%%%s%%'", jsonColumnName, jsonKey, value));
				break;
			case STARTS_WITH:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%s%%'", jsonColumnName, jsonKey, value));
				break;
			case ENDS_WITH:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%%%s'", jsonColumnName, jsonKey, value));
				break;
			case IS_EMPTY:
				conditionString.append(String.format("(%s->>'%s') = ''", jsonColumnName, jsonKey));
				break;
			case IS_NOT_EMPTY:
				conditionString.append(String.format("(%s->>'%s') IS NOT NULL AND (%s->>'%s') != ''", jsonColumnName,
						jsonKey, jsonColumnName, jsonKey));
				break;
			case IS_NULL:
				conditionString.append(String.format("(%s->>'%s') IS NULL", jsonColumnName, jsonKey));
				break;
			case IS_NOT_NULL:
				conditionString.append(String.format("(%s->>'%s') IS NOT NULL", jsonColumnName, jsonKey));
				break;
			case GREATER_THAN:
				conditionString.append(
						String.format("CAST(NULLIF(%s->>'%s', '') AS DOUBLE PRECISION) > CAST(%s AS DOUBLE PRECISION)",
								jsonColumnName, jsonKey, value));
				break;
			case LESS_THAN:
				conditionString.append(
						String.format("CAST(NULLIF(%s->>'%s', '') AS DOUBLE PRECISION) < CAST(%s AS DOUBLE PRECISION)",
								jsonColumnName, jsonKey, value));
				break;
			case BEFORE:
				conditionString.append(
						String.format("CAST(%s->>'%s' AS date) < CAST('%s' AS date)", jsonColumnName, jsonKey, value));
				break;
			case AFTER:
				conditionString.append(
						String.format("CAST(%s->>'%s' AS date) > CAST('%s' AS date)", jsonColumnName, jsonKey, value));
				break;
			case IN:
				String[] values = value.split(",");
				String formattedValues = Arrays.stream(values).map(v -> String.format("'%s'", v.trim()))
						.collect(Collectors.joining(", "));
				conditionString.append(
						String.format("(%s->>'%s') = ANY (ARRAY[%s])", jsonColumnName, jsonKey, formattedValues));
				break;
			case NOT_IN:
				String[] valuesNotIn = value.split(",");
				String formattedValuesNotIn = Arrays.stream(valuesNotIn).map(v -> String.format("'%s'", v.trim()))
						.collect(Collectors.joining(", "));
				conditionString.append(
						String.format("(%s->>'%s') != ALL (ARRAY[%s])", jsonColumnName, jsonKey, formattedValuesNotIn));
				break;
			case IS_TRUE:
				conditionString.append(String.format("CAST(%s->>'%s' AS boolean) = true", jsonColumnName, jsonKey));
				break;
			case IS_FALSE:
				conditionString.append(String.format("CAST(%s->>'%s' AS boolean) = false", jsonColumnName, jsonKey));
				break;
			}
		} else {
			switch (condition) {
			case EQUAL:
				conditionString.append(String.format("%s ILIKE '%s'", column, value));
				break;
			case NOT_EQUAL:
				conditionString.append(String.format("%s NOT ILIKE '%s'", column, value));
				break;
			case CONTAINS:
				conditionString.append(String.format("%s ILIKE '%%%s%%'", column, value));
				break;
			case DOES_NOT_CONTAIN:
				conditionString.append(String.format("%s NOT ILIKE '%%%s%%'", column, value));
				break;
			case STARTS_WITH:
				conditionString.append(String.format("%s ILIKE '%s%%'", column, value));
				break;
			case ENDS_WITH:
				conditionString.append(String.format("%s ILIKE '%%%s'", column, value));
				break;
			case IS_EMPTY:
				conditionString.append(String.format("%s = ''", column));
				break;
			case IS_NOT_EMPTY:
				conditionString.append(String.format("%s IS NOT NULL AND %s != ''", column, column));
				break;
			case IS_NULL:
				conditionString.append(String.format("%s IS NULL", column));
				break;
			case IS_NOT_NULL:
				conditionString.append(String.format("%s IS NOT NULL", column));
				break;
			case GREATER_THAN:
				conditionString.append(String.format(
						"CAST(NULLIF(%s, '') AS DOUBLE PRECISION) > CAST(%s AS DOUBLE PRECISION)", column, value));
				break;
			case LESS_THAN:
				conditionString.append(String.format(
						"CAST(NULLIF(%s, '') AS DOUBLE PRECISION) < CAST(%s AS DOUBLE PRECISION)", column, value));
				break;
			case BEFORE:
				conditionString.append(String.format("CAST(%s AS date) < CAST('%s' AS date)", column, value));
				break;
			case AFTER:
				conditionString.append(String.format("CAST(%s AS date) > CAST('%s' AS date)", column, value));
				break;
			case IN:
				String[] valuesIn = value.split(",");
				String formattedValuesIn = Arrays.stream(valuesIn).map(v -> String.format("'%s'", v.trim()))
						.collect(Collectors.joining(", "));
				conditionString.append(String.format("%s IN (%s)", column, formattedValuesIn));
				break;
			case NOT_IN:
				String[] valuesNotIn = value.split(",");
				String formattedValuesNotIn = Arrays.stream(valuesNotIn).map(v -> String.format("'%s'", v.trim()))
						.collect(Collectors.joining(", "));
				conditionString.append(String.format("%s NOT IN (%s)", column, formattedValuesNotIn));
				break;
			case IS_TRUE:
				conditionString.append(String.format("%s = true", column));
				break;
			case IS_FALSE:
				conditionString.append(String.format("%s = false", column));
			}
		}
		return conditionString.toString();
	}

}
