package com.generic.common;

import java.util.Map;

/**
 * DTO 
 */
public class GenericUpdationDTO {

	private Map<String, Integer> mapOfCondtions;
	private Map<String, Integer> mapofValues;
	private Map<Integer, String> rowError;
	private ResponseError error;
	private int successCount;

	public Map<String, Integer> getMapOfCondtions() {
		return mapOfCondtions;
	}

	public void setMapOfCondtions(Map<String, Integer> mapOfCondtions) {
		this.mapOfCondtions = mapOfCondtions;
	}

	public Map<String, Integer> getMapofValues() {
		return mapofValues;
	}

	public void setMapofValues(Map<String, Integer> mapofValues) {
		this.mapofValues = mapofValues;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

	public Map<Integer, String> getRowError() {
		return rowError;
	}

	public void setRowError(Map<Integer, String> rowError) {
		this.rowError = rowError;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	@Override
	public String toString() {
		return "GenericUpdationDTO [mapOfCondtions=" + mapOfCondtions
				+ ", mapofValues=" + mapofValues + ", rowError=" + rowError
				+ ", error=" + error + ", successCount=" + successCount + "]";
	}

}
