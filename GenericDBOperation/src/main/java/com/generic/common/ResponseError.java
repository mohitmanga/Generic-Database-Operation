/**
 * 
 */
package com.generic.common;

/**
 * The Class ResponseError.
 */
public class ResponseError {

	/** The error code. */
	private String errorCode;

	/** The error description. */
	private String errorDescription;

	/**
	 * Gets the error code.
	 * 
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 * 
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(final String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error description.
	 * 
	 * @return the error description
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * Sets the error description.
	 * 
	 * @param errorDescription
	 *            the new error description
	 */
	public void setErrorDescription(final String errorDescription) {
		this.errorDescription = errorDescription;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseError [errorCode=");
		builder.append(errorCode);
		builder.append(", errorDescription=");
		builder.append(errorDescription);
		builder.append("]");
		return builder.toString();
	}
	
}
