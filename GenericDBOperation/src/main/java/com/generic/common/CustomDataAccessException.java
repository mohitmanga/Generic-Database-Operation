package com.generic.common;

import org.springframework.dao.DataAccessException;

/**
 * Custom DataAccessException for Generic DB operation
 * screen
 */
public class CustomDataAccessException extends DataAccessException {

	private static final long serialVersionUID = 1667862241288730114L;

	public CustomDataAccessException(String msg) {
		super(msg);
	}
}
