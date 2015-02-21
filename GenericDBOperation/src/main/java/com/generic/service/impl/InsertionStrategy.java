package com.generic.service.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

@Component("insertionstrategy")
public class InsertionStrategy extends GenericOperationStrategy {
	
	@Autowired
	private GenericDBOperationDAO operationDAO;
	
	@Autowired
	private IDBMetaDataService dbMetaDataService;
	
	@Autowired
	private CustomPropertyPlaceholder customPropertyPlaceholder;

	
	private static final Logger logger = LoggerFactory.getLogger(InsertionStrategy.class);

	@Override
	public GenericUpdationDTO validateExcel(File excelFile, String tableName) {
		GenericUpdationDTO dto = new GenericUpdationDTO();
		ResponseError error = new ResponseError();
		try {
			 Set<String> notNullCols = dbMetaDataService.getNotNullColumns(tableName);
			 /**
			  * Remove Logging columns such as created by and created date. This will be managed
			  * by application.
			  */
			 notNullCols.remove(customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_by"));
			 notNullCols.remove(customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_date"));
		     /**
		      * Remove auto increment column
		      */
			 notNullCols.removeAll(dbMetaDataService.getAutoIncrementCols(tableName));
			 Set<String> cols = new HashSet<String>();
			 Workbook workbook = WorkbookFactory.create(excelFile);
			 Map<String, Integer> mapOfCondtions = new LinkedHashMap<String, Integer>();
			 if(workbook.getNumberOfNames() != 0){
				 mapOfCondtions = getMapFromNamedRange(workbook, Constants.INSERT_NAME_RANGE);
				 if(mapOfCondtions.size()!=0){
					 cols = mapOfCondtions.keySet();
					 dto.setMapOfCondtions(mapOfCondtions);
					 if(!cols.containsAll(notNullCols)){
						 notNullCols.removeAll(cols);
				    	 error.setErrorCode("400");
				    	 error.setErrorDescription("Please upload valid excel sheet. Please mention all the non null columns. Missing keys ->"+notNullCols);
						 logger.warn("Sheet upload doesn't contain all non null columns of the table, Missing column(s) ->{}",notNullCols);
					 }else{
						 // NOP
					 }
				 }else{
					 logger.warn("No named ranges have been defined. Please define insert named ranges");
			    	 error.setErrorCode("400");
			    	 error.setErrorDescription("Please upload valid excel sheet. Please specify Condition and Value columns"); 
				 }
			 }else{
				 logger.warn("No named ranges have been defined. Please define insert named ranges");
		    	 error.setErrorCode("400");
		    	 error.setErrorDescription("Please upload valid excel sheet. Please specify Condition and Value columns");
			 }
			 
		} catch (Exception e){ 
			logger.error("Exception occured during validating excel for insertion",e);
			error.setErrorCode("404");
			error.setErrorDescription("An error has occured. Please contact system admin for the same.");
		}
		dto.setError(error);
		return dto;
	}

	@Override
	public Map<String, Integer> extractQueryStatements(GenericUpdationDTO dto, File excelFile, String tableName, String operation, String userId )  {
		Map<String, Integer> insertQueries = new HashMap<String, Integer>();
		Object [] args = new Object[3];
		try {
			 StringBuilder log = new StringBuilder();
			 String createdBy = customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_by");
			 String createDate = customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_date");
			 Workbook workbook = WorkbookFactory.create(excelFile);
			 Sheet sheet = workbook.getSheetAt(0);
			 Map<String, Integer> mapOfCondtions = dto.getMapOfCondtions();
			 StringBuilder cols = new StringBuilder(StringUtils.join(mapOfCondtions.keySet(),","));
			 /**
			  * Add logging information
			  */
			 if(StringUtils.isNotEmpty(createdBy)){
				 cols.append(" , ").append(createdBy);
				 log.append(",").append("'"+userId+"'");
			 }
			 if(StringUtils.isNotEmpty(createDate)){
				 log.append(",").append(" NOW()");
				 cols.append(" , ").append(createDate);
			 }
			 MessageFormat message = new MessageFormat("INSERT INTO {0} ( {1} ) VALUES ( {2} ) ");
		     for(Row row : sheet){
				StringBuilder whereQuery = new StringBuilder();
		    	if(row.getRowNum()!=0){
				  String seperatorOne ="";
		    		for(Map.Entry<String, Integer> entry : mapOfCondtions.entrySet()){
						 whereQuery.append(seperatorOne).append(getCellValue(row.getCell(entry.getValue())));
						 seperatorOne = " , ";
		    		}
					 args[0] = tableName;
					 args[1] = cols;
					 args[2] = whereQuery.append(log);
					 insertQueries.put(message.format(args), row.getRowNum()+1);
		    	}
		     }
		} catch (Exception e) {			
		  logger.error("Exception occured during validating excel",e);
		}
		return insertQueries;
	
	}

		@Override
		public GenericUpdationDTO executeQueries(final Map<String, Integer> queries) {
			return operationDAO.executeQueries(queries);
		}
}
