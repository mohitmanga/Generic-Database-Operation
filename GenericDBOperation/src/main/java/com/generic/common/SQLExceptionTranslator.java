package com.generic.common;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

/**
 *Custom SQL exception translator for interpreting
 *SQL generated errors
 */
public class SQLExceptionTranslator extends SQLErrorCodeSQLExceptionTranslator {
	
	private Logger logger = LoggerFactory.getLogger(SQLExceptionTranslator.class);
	
	/**
	 * Custom method for translating DB exception into user readable exception.
	 */
	protected DataAccessException customTranslate(String task, String sql,SQLException sqlex) {
		try{
			logger.debug("Exception before translation",sqlex);
			StringBuilder msg = new StringBuilder();
			msg.append(StringUtils.join(sqlex.getMessage().split(",  message from server:"),","));
			return new CustomDataAccessException(msg.toString());
		}catch(Exception e){
			logger.error("Exception while translating db exception",e);
			return new CustomDataAccessException(sqlex.getMessage());
			
		}
	}
}
