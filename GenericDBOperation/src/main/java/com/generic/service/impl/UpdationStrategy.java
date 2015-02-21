package com.generic.service.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generic.common.GenericUpdationDTO;
import com.generic.common.ResponseError;
import com.generic.constants.Constants;
import com.generic.dao.GenericDBOperationDAO;
import com.generic.service.GenericOperationStrategy;
import com.generic.service.IDBMetaDataService;
import com.generic.util.CustomPropertyPlaceholder;

@Component("updationStrategy")
public class UpdationStrategy extends GenericOperationStrategy {
	
	@Autowired
	private GenericDBOperationDAO operationDAO;
	
	@Autowired
	private IDBMetaDataService dbMetaDataService;
	
	@Autowired
	private CustomPropertyPlaceholder customPropertyPlaceholder;
	
	private static final Logger logger = LoggerFactory.getLogger(UpdationStrategy.class);

	
	@Override
	public GenericUpdationDTO validateExcel(File excelFile, String tableName) {
		GenericUpdationDTO dto = new GenericUpdationDTO();
		ResponseError error = new ResponseError();
		try {
			 Set<String> primaryKeys = new HashSet<String>(Arrays.asList(dbMetaDataService.getPrimaryKeys(tableName)));
			 Workbook workbook = WorkbookFactory.create(excelFile);
			 Set<String> cols = new HashSet<String>();
			 Map<String, Integer> mapOfCondtions = null;
			 Map<String, Integer> mapofValues = null;
			 if(workbook.getNumberOfNames() != 0){
				 /**
				  * Check that named range has been defined in excel.
				  */
				mapOfCondtions = getMapFromNamedRange(workbook, Constants.CONDITION_NAMED_RANGE);
				mapofValues = getMapFromNamedRange(workbook, Constants.VALUE_NAME_RANGE);
				if(mapOfCondtions.size()!=0){
					if(mapofValues.size()!=0){
						/**
						 * Get all the Conditional Columns
						 */
						 cols = mapOfCondtions.keySet();
					     dto.setMapOfCondtions(mapOfCondtions);
					     /**
					      * Get all the Value Columns
					      */
					     dto.setMapofValues(mapofValues);
					     
						 if(cols.containsAll(primaryKeys)){
							//nop
						 }else{
							primaryKeys.removeAll(cols);
					    	error.setErrorCode("400");
					    	error.setErrorDescription("Please upload valid excel sheet. Please mention all the primary keys. Missing keys ->"+primaryKeys);
							logger.warn("Sheet upload doesn't contain all the primary keys of the table, Missing column(s) ->{}",primaryKeys);
						 }
				    }else{
				    	logger.warn("No values column(s) have been defined");
				    	error.setErrorCode("400");
				    	error.setErrorDescription("Please upload valid excel sheet. Please specify value columns");
				    }
				 }else{
					 logger.warn("No condition column(s) have been defined");
			    	 error.setErrorCode("400");
			    	 error.setErrorDescription("Please upload valid excel sheet. Please specify Condition columns");
				 }
			 }else{
				 logger.warn("No named ranges have been defined. Please define condtion and values as named ranges");
		    	 error.setErrorCode("400");
		    	 error.setErrorDescription("Please upload valid excel sheet. Please specify Condition and Value columns");
			 }
		} catch (Exception e) {
			logger.error("Exception occured during validating excel",e);
			error.setErrorCode("404");
			error.setErrorDescription("An error has occured. Please contact system admin for the same.");
		}
		dto.setError(error);
		return dto;
	}

	@Override
	public Map<String, Integer> extractQueryStatements(GenericUpdationDTO dto, File excelFile, String tableName, String operation, String userId ) {
		Map<String, Integer> updateQueries = new HashMap<String, Integer>();
		Object [] args = new Object[3];
		try {
			 StringBuilder log = new StringBuilder();
			 String updatedBy = customPropertyPlaceholder.getProperty("generic_"+tableName+"_Update_by");
			 String updatedDate = customPropertyPlaceholder.getProperty("generic_"+tableName+"_Update_date");
			 Workbook workbook = WorkbookFactory.create(excelFile);
			 Sheet sheet = workbook.getSheetAt(0);
			 Map<String, Integer> mapOfCondtions = dto.getMapOfCondtions();
			 Map<String, Integer> mapofValues = dto.getMapofValues();
			 /**
			  * Add Logging Information
			  */
			 if(org.apache.commons.lang.StringUtils.isEmpty(updatedBy)){
				 log.append(",").append(updatedBy+"= "+"'"+userId+"'");
			 }
			 if(org.apache.commons.lang.StringUtils.isEmpty(updatedDate)){
				 log.append(",").append(updatedDate+"= NOW()");
			 }
			 MessageFormat message = new MessageFormat("UPDATE {0} SET {1} where {2}");
		     for(Row row : sheet){
				StringBuilder whereQuery = new StringBuilder();
				StringBuilder setQuery = new StringBuilder();
		    	if(row.getRowNum()!=0){
				  String seperatorOne ="";
				  String seperatorTwo ="";
		    		for(Map.Entry<String, Integer> entry : mapOfCondtions.entrySet()){
						 whereQuery.append(seperatorOne).append(entry.getKey()+" = "+ getCellValue(row.getCell(entry.getValue())));
						 seperatorOne = " AND ";
		    		}
		    		for(Map.Entry<String, Integer> entry : mapofValues.entrySet()){
		    			setQuery.append(seperatorTwo).append(entry.getKey()+" = "+ getCellValue(row.getCell(entry.getValue())));
						seperatorTwo =" , ";
		    		}
					 args[0] = tableName;
					 args[1] = setQuery.append(log);
					 args[2] = whereQuery;
					 updateQueries.put(message.format(args), row.getRowNum()+1);
		    	}
		     }
		} catch (Exception e) {			
		  logger.error("Exception occured during extracting database queries excel",e);
		}
		return updateQueries;
	}
	
		@Override
		public GenericUpdationDTO executeQueries(final Map<String, Integer> queries) {
			return operationDAO.executeQueries(queries);
		}
}
