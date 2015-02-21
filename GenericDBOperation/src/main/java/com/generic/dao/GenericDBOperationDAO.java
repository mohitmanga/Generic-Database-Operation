package com.generic.dao;

import java.util.Map;

import com.generic.common.GenericUpdationDTO;

public interface GenericDBOperationDAO  {

	/**
	 * Execute Queries
	 * @param queries
	 * @return
	 */
	public GenericUpdationDTO executeQueries(final Map<String, Integer> queries);
	
}
