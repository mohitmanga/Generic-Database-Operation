package com.generic.forms;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class FileUploadForm {
	
	private String name;
	private CommonsMultipartFile file;
	private String source;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CommonsMultipartFile getFile() {
		return file;
	}
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
		this.name = file.getOriginalFilename();
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public String toString() {
		return "UploadForm [name=" + name + ", file=" + file + ", source="
				+ source + "]";
	}
	
	
}
