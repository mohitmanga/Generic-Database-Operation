package com.generic.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.generic.common.GenericUpdationDTO;
import com.generic.common.SQLExceptionTranslator;
import com.generic.dao.GenericDBOperationDAO;

@Repository
public class GenericDBOperationDAOImpl implements GenericDBOperationDAO {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	TransactionTemplate transactionTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(GenericDBOperationDAOImpl.class);

	public GenericUpdationDTO executeQueries(final Map<String, Integer> queries) {
		final Map<Integer, String> rowError = new HashMap<Integer, String>();
		SQLExceptionTranslator tr = new SQLExceptionTranslator();
		jdbcTemplate.setExceptionTranslator(tr);
		final GenericUpdationDTO dto = new GenericUpdationDTO();
		final StringBuilder message = new StringBuilder("Sorry Operation cannot be performed due incorrect data. Row(s) with incorrect data are -->");
        try{  
        	transactionTemplate.execute(new TransactionCallback<Object>()  
            {  
                public Object doInTransaction(TransactionStatus transactionStatus){
                	int count = 0;
                	Boolean result = true;
                	Iterator<Entry<String, Integer>> iterator = queries.entrySet().iterator();
                	while(iterator.hasNext()){
                		Entry<String, Integer> entry = iterator.next();
                		try{
                			count += jdbcTemplate.update(entry.getKey());
                		} catch (Exception e) {
                			rowError.put(entry.getValue(), e.getMessage());
                			result = false;
                			message.append(",").append(entry.getValue());
						}
                		// check for last element
                		if(!iterator.hasNext() && !result){
                			throw new RuntimeException(message.toString());
                		}
                	}
                	dto.setSuccessCount(count);
                    return result;  
                }  
            });
		}catch(Exception e){
			logger.error("Exception occured while updating database.",e);
			dto.setRowError(rowError);
			dto.setSuccessCount(0);
		}
        return dto;
	}
}
