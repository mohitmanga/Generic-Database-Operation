package com.generic.service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import com.generic.common.GenericUpdationDTO;

public abstract class GenericOperationStrategy {

	
	/**
	 * Validates excel sheet that has been uploaded. Current validation
	 * check for if all the primary keys are being added in case of DB update operation and
	 * checks for all not null columns in case of DB insert operation
	 * @param excelFile Excel File
	 * @param tableName Table Name
	 * @return
	 */
	public abstract GenericUpdationDTO validateExcel(final File excelFile, final String tableName);
	
	/**
	 * Converts data from excel sheet into DB Queries
	 * @param dto GenericUpdationDTO
	 * @param excelFile Excel File
	 * @param tableName Table Name
	 * @return
	 */
	public abstract Map<String, Integer> extractQueryStatements(final GenericUpdationDTO dto, final File excelFile, final String tableName,final String operation, final String userId);
	
	/**
	 * Executes DB Queries in a batch
	 * @param queries
	 * @return
	 */
	public abstract GenericUpdationDTO executeQueries(final Map<String, Integer> queries);
	
	/**
	 * Gives a map of DB Columns versus Excel col Name from the named ranges
	 * being provided.
	 * @param workbook Excel Workbook
	 * @param namedRange Excel named range.
	 * @return
	 */
	public Map<String, Integer> getMapFromNamedRange(Workbook workbook, String namedRange){
		 Sheet sheet = workbook.getSheetAt(0);
		 Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		 if(workbook.getNameIndex(namedRange) >= 0){
			 Name aNamedCell = workbook.getNameAt(workbook.getNameIndex(namedRange));
		     AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
		     CellReference[] crefs = aref.getAllReferencedCells();
		     for (int i=0; i<crefs.length; i++) {
		        Row r = sheet.getRow(crefs[i].getRow());
		        Cell c = r.getCell(crefs[i].getCol());
		        if(c != null && (!StringUtils.isEmpty(c.getStringCellValue()) || c.getStringCellValue().trim().length() != 0 )){
		        	map.put(c.getStringCellValue(), c.getColumnIndex());
		        }
		     }
		 }
		return map;
	}
	
	/**
	 * Get value of excel column
	 * @param cell
	 * @return
	 */
	public  Object getCellValue(final Cell cell){
		Object value = null;
		DataFormatter format = new DataFormatter();
	    if (cell!=null) {
	        switch (cell.getCellType()) {
	            case Cell.CELL_TYPE_BOOLEAN:
	            	value = cell.getBooleanCellValue();
	                break;
	            case Cell.CELL_TYPE_NUMERIC:
	            	value = format.formatCellValue(cell);
	                break;
	            case Cell.CELL_TYPE_STRING:
	            	value = StringUtils.trim(cell.getStringCellValue());
	                break;
	            case Cell.CELL_TYPE_BLANK:
	                break;
	            case Cell.CELL_TYPE_ERROR:
	            	value = cell.getErrorCellValue();
	                break;
	            case Cell.CELL_TYPE_FORMULA:
	                break;
	        }
	    }
	    return (value != null) ? "'"+value+"'" : null;
	}
}
