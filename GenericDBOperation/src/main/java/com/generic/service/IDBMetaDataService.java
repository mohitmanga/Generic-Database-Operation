package com.generic.service;

import java.sql.SQLException;
import java.util.Set;

public interface IDBMetaDataService {

	/**
	 * GET all the columns of table in database
	 * @param table Table Name
	 * @return
	 * @throws SQLException
	 */
	public abstract Set<String> getColumns(String table) throws SQLException;

	/**
	 * GET all primary keys of table
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public abstract String[] getPrimaryKeys(String table) throws SQLException;

	/**
	 * GET all not null columns for table
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public abstract Set<String> getNotNullColumns(String tableName)
			throws SQLException;

	/**
	 * GET AutoIncrement cols.
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public abstract Set<String> getAutoIncrementCols(String tableName)
			throws SQLException;

}