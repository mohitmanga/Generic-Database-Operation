package com.generic.forms;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Generic Screen form
 *
 */
public class GenericUpdationForm {

	private CommonsMultipartFile file;
	private String tableName;
	private String operation;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
