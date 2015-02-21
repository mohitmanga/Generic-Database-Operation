package com.generic.controller;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.generic.common.GenericUpdationDTO;
import com.generic.constants.Constants;
import com.generic.forms.FileUploadForm;
import com.generic.forms.GenericUpdationForm;
import com.generic.service.GenericOperationStrategy;
import com.generic.service.IDBMetaDataService;
import com.generic.util.CustomPropertyPlaceholder;

/**
 * Generic Database operation controller.
 */
@Controller
@RequestMapping(value="/genericutil")
public class GenericDBOperationController implements InitializingBean {
	
	@Resource (name= "insertionstrategy")
	GenericOperationStrategy insertionStratergy;

	@Resource(name= "updationStrategy")
	GenericOperationStrategy updationStrategy;
	
	@Autowired
	private IDBMetaDataService dbMetaDataService;
	
	@Autowired
	private CustomPropertyPlaceholder customPropertyPlaceholder;
	
	private Logger logger =LoggerFactory.getLogger(GenericDBOperationController.class);
	
	/**
	 * Download file to temporary directory(Based on operating system).
	 */
	private static final String filePath = System.getProperty("java.io.tmpdir");

	private Set<String> tableList = null;
	
	/**
	 * Initial loading for Spring form and getting jsp
	 * from tiles definition.
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model){
	    model.put("tables", tableList);
		GenericUpdationForm form = new GenericUpdationForm();
		model.addAttribute("CSV_UPLOAD_FORM", form);
		return "genericutil";
	}
	

	/**
	 * Method to Perform generic database operation
	 * @param httpServletResponse HttpServletResponse
	 * @param genericUpdationForm generic screen form
	 * @param result Binding result
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void genericOperation(final HttpServletResponse httpServletResponse, @ModelAttribute("CSV_UPLOAD_FORM") GenericUpdationForm genericUpdationForm 
				, BindingResult result) throws IllegalStateException, IOException {

		CommonsMultipartFile files = genericUpdationForm.getFile();
		String fileExtension = FilenameUtils.getExtension(genericUpdationForm.getFile().getOriginalFilename());
		FileUploadForm form = new FileUploadForm();
		form.setFile(files);
		form.setName(files.getOriginalFilename());
		Map<String, String> response = new HashMap<String, String>();
		
		if(tableList.contains(genericUpdationForm.getTableName())){
			/**
			 * Specific format's are allowed
			 */
			if("xls".equals(fileExtension) || "xlsx".equals(fileExtension)){
				StopWatch watch = new StopWatch();
				try {
					File file = new File(filePath+File.separator+"generic_updation.xlsx");
					form.getFile().getFileItem().write(file);
					watch.start();
					if(Constants.UPDATE_OPERATION.equals(genericUpdationForm.getOperation())){
						response = executeOperation(updationStrategy, file, genericUpdationForm.getTableName(), "TestUser", genericUpdationForm.getOperation());
					}else{
						response = executeOperation(insertionStratergy, file, genericUpdationForm.getTableName(), "TestUser", genericUpdationForm.getOperation());
					}
					watch.stop();
					logger.debug("Total time taken in doing operation -->{}", watch.getTotalTimeSeconds());
				} catch (Exception e) {
					logger.error("Exception occured",e);
					response.put("result", "error");
					response.put("message","An error has occured. Please contact system admin for the same.");
				}
			}else{
				response.put("result", "error");
				response.put("message","Please upload correct format file(excel).");
			}
		}else{
			response.put("result", "error");
			response.put("message","Please select valid table name");
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(httpServletResponse.getWriter(), response);
	}
	

	/**
	 * Delegate to particular handler based on operation
	 * selected
	 * @param strategy Handler
	 * @param file File object
	 * @param tableName Table Name
	 * @param userId User performing operation
	 * @param operation Operation Selected
	 * @return
	 */
	private Map<String, String> executeOperation(GenericOperationStrategy strategy, File file, String tableName, String userId, String operation){
		Map<String, String> response = new HashMap<String, String>();
		Map<String, Integer> queries = new HashMap<String, Integer>();
		GenericUpdationDTO dto = strategy.validateExcel(file,tableName);
		if(dto.getError().getErrorCode() != null){
			response.put("result", "error");
			response.put("message", dto.getError().getErrorDescription());
		}else{
			queries = strategy.extractQueryStatements(dto, file, tableName, operation, userId);
			if(queries.size() != 0){
				GenericUpdationDTO resultDto = strategy.executeQueries(queries);
				if(resultDto.getSuccessCount() == 0 && resultDto.getRowError() != null && resultDto.getRowError().size() !=  0 ){
					response.put("result", "error");
					response.put("message", getErrors(resultDto.getRowError()));
				}else{
					response.put("result", "success");
					response.put("message", "Data updated successfully. Total number of row(s) affected are :"+ resultDto.getSuccessCount());
				}
			}else{
				response.put("result", "error");
				response.put("message", "Please upload valid excel");
			}
		}
		return response;
	}
	
	/**
	 * Provides information regarding the error(s) that came up
	 * during the execution of database queries
	 * @param errors Map of error i.e Map<ExcelRowNumber, Cause>
	 * @return
	 */
	private String getErrors(Map<Integer, String> errors) {

		String errorTemplate = new String("<tr><td>{0}</td><td>{1}</td></tr>");
		StringBuilder sb = new StringBuilder("<div class='errorMsgDiv'>Sorry. Operation can not be performed due to error in data. </br>" +
				"<table cellspacing='4' cellpadding='7' class='errmsgTable' >" +
				"<tr> <th>Row</th> <th>Cause</th></tr>");

		for (Map.Entry<Integer, String> entry : errors.entrySet()) {
			Object[] parms = new Object[] {entry.getKey(), StringUtils.replace(entry.getValue(), "\"", "")};
			sb.append(MessageFormat.format(errorTemplate, parms));
		}
		sb.append("</table></div>");
		return sb.toString();
	}
	
	/**
	 * Download excel giving template for selected operation
	 * @param tableName Table Name
	 * @param operation Operation
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/dwnldtemplate/{tableName}/{operation}")
	public ModelAndView downloadTemplate(@PathVariable String tableName, @PathVariable String operation) {
		ModelAndView modelAndView = new ModelAndView("GenericTemplate", "GenericTemplate", tableName);
		try {
			modelAndView.addObject("PrimaryKeys",  new HashSet<String>(Arrays.asList(dbMetaDataService.getPrimaryKeys(tableName))));
			modelAndView.addObject("Columns", dbMetaDataService.getColumns(tableName));
			modelAndView.addObject("AutoIncrementCols", dbMetaDataService.getAutoIncrementCols(tableName));
			modelAndView.addObject("Operation", operation);
		} catch (Exception e) {
			modelAndView.setViewName("errorPage");
			modelAndView.addObject("text", "There seems to be some issue.<br/><br/>Please try after some time.");
		}
		return modelAndView;
	}


	public void afterPropertiesSet() throws Exception {
		tableList = customPropertyPlaceholder.getProperty("generic.updation.tablelist") == null ? new HashSet<String>() : new HashSet<String>(
				Arrays.asList(customPropertyPlaceholder.getProperty("generic.updation.tablelist").split(",")));
	}
}
