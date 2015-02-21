package com.generic.excel;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.generic.constants.Constants;
import com.generic.util.CustomPropertyPlaceholder;

public class GenericDBOperationTemplateExcel extends AbstractExcelView {

	@Autowired
	private CustomPropertyPlaceholder customPropertyPlaceholder;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tableName =  (String)model.get("GenericTemplate");
		String operation = (String)model.get("Operation");
		Set<String> primaryKeys =  (Set<String>)model.get("PrimaryKeys");
		Set<String> columns =  (Set<String>)model.get("Columns");
		Set<String> autoIncrementColumns =  (Set<String>)model.get("AutoIncrementCols");
	    String fileName = String.format("Generic_%s_%s", operation, tableName);
	    response.setHeader("content-disposition", "attachment; filename=" + fileName+".xls");
	    createExcel(workbook, tableName, autoIncrementColumns, primaryKeys, columns, operation);
	}

	/**
	 * Method to create excel giving template used for
	 * inserting/ update database.
	 * @param workBook
	 * @param tableName
	 * @param primaryKeys
	 * @param columns
	 */
	 private void createExcel(HSSFWorkbook workBook, String fullTableName,Set<String> autoIncCols, Set<String> primaryKeys, Set<String> columns, String operation) {
		 String tableName = fullTableName.length() > 31 ? fullTableName.substring(0, 30) : fullTableName;
		 HSSFSheet sheet = workBook.createSheet(tableName);
		 int rowno = 0;
		 int colno = 0;
		 String reference = null;
		 String nameOfRange = null;
		 
		 HSSFRow row = sheet.createRow(rowno);
		 HSSFCellStyle style = styleExcel(workBook);
		 /**
		  * Remove logging cols. Since this information will be added
		  * during the operation through application
		  */
		 columns.remove( customPropertyPlaceholder.getProperty("generic_"+tableName+"_Update_by"));
		 columns.remove(customPropertyPlaceholder.getProperty("generic_"+tableName+"_Update_date"));
		 columns.remove( customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_by"));
		 columns.remove(customPropertyPlaceholder.getProperty("generic_"+tableName+"_Insert_date"));

		 if(Constants.UPDATE_OPERATION.equals(operation)){
			 /**
			  * Iterate through primary keys
			  */
			 for(String colName : primaryKeys){
				 Cell cell = row.createCell(colno);
				 cell.setCellValue(colName);
				 sheet.autoSizeColumn(colno);
				 sheet.setDefaultColumnStyle(colno, style);
				 ++colno;
			 }
		     reference = tableName+"!$A$1:$"+CellReference.convertNumToColString(primaryKeys.size()-1)+"$1"; // area reference
		     createNamedRange(workBook, Constants.CONDITION_NAMED_RANGE, reference);
		     nameOfRange = Constants.VALUE_NAME_RANGE;
		     columns.removeAll(primaryKeys);
		     reference = tableName+"!$"+CellReference.convertNumToColString(primaryKeys.size())+"$1:$"+CellReference.convertNumToColString(primaryKeys.size()+columns.size()-1)+"$1"; // area reference
		 }else{
			 /**
			  * Remove auto increment column in case of 
			  * insert operation.
			  */
			 columns.removeAll(autoIncCols);
			 nameOfRange = Constants.INSERT_NAME_RANGE;
			 reference = tableName+"!$A$1:$"+CellReference.convertNumToColString(columns.size()-1)+"$1"; // area reference
		 }
	     /**
	      * Iterate through values
	      */
	     for(String cols : columns){
	    	 Cell cell = row.createCell(colno);
			 cell.setCellValue(cols);
			 cell.setCellStyle(style);
			 sheet.autoSizeColumn(colno);
			 sheet.setDefaultColumnStyle(colno, style);
			 ++colno;
	     }
	     
	     createNamedRange(workBook, nameOfRange, reference);
	 }

	 /**
	  * Create Name Range
	  * @param workBook
	  * @param nameOfRange
	  * @param referenceName
	  */
	 private void createNamedRange(Workbook workBook, String nameOfRange, String referenceName){
		 Name namedCell = workBook.createName();
		 namedCell.setNameName(nameOfRange);
		 namedCell.setRefersToFormula(referenceName);
		 namedCell.setComment("Please note these named range are used to identify "+nameOfRange);
	 }
	 
	 /**
	  * Used to Styling Excel.
	  * @param workBook
	  * @return
	  */
	 private HSSFCellStyle styleExcel(HSSFWorkbook workBook){
		 DataFormat format =  workBook.createDataFormat();
		 HSSFCellStyle style = workBook.createCellStyle();
		 style.setDataFormat(format.getFormat("@"));
		 return style;
	 }

	public CustomPropertyPlaceholder getCustomPropertyPlaceholder() {
		return customPropertyPlaceholder;
	}

	public void setCustomPropertyPlaceholder(
			CustomPropertyPlaceholder customPropertyPlaceholder) {
		this.customPropertyPlaceholder = customPropertyPlaceholder;
	}
}
