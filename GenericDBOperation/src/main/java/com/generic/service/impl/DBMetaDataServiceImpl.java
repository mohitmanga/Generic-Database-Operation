package com.generic.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.generic.service.IDBMetaDataService;

/**
 * Service Class for getting meta data regarding
 * tables of database
 *
 */
@Service
public class DBMetaDataServiceImpl implements InitializingBean, IDBMetaDataService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * SQL Conneciton
	 */
	private Connection connection;
	
	/* (non-Javadoc)
	 * @see com.generic.service.IDBMetaDataService#getColumns(java.lang.String)
	 */
	public Set<String> getColumns(String table) throws SQLException {
		ResultSet rs = connection.getMetaData().getColumns(null, null, table, null);
		Set<String> list = new HashSet<String>();
		while (rs.next()) {
			list.add(rs.getString("COLUMN_NAME"));
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.generic.service.IDBMetaDataService#getPrimaryKeys(java.lang.String)
	 */
	public  String[] getPrimaryKeys(String table) throws SQLException {
		ResultSet rs = connection.getMetaData().getPrimaryKeys(null, null, table);
		List<String> list = new ArrayList<String>();
		while (rs.next()) {
			list.add(rs.getString("COLUMN_NAME"));
		}
		return list.toArray(new String[] {});
	}
    
    /* (non-Javadoc)
	 * @see com.generic.service.IDBMetaDataService#getNotNullColumns(java.lang.String)
	 */
    public Set<String> getNotNullColumns(String tableName) throws SQLException{
    	Set<String> cols = new HashSet<String>();
		ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, null);
		while (rs.next()) {
			if(!rs.getBoolean("IS_NULLABLE")){
				cols.add(rs.getString("COLUMN_NAME"));
			}
		}
    	return cols;
    }
    
    /* (non-Javadoc)
	 * @see com.generic.service.IDBMetaDataService#getAutoIncrementCols(java.lang.String)
	 */
    public Set<String> getAutoIncrementCols(String tableName) throws SQLException{
    	Set<String> cols = new HashSet<String>();
    	Statement statement = connection.createStatement();
	    ResultSet resultSet = statement.executeQuery("SELECT * FROM "+tableName+" LIMIT 1 ");
	    ResultSetMetaData metadata = resultSet.getMetaData();
	    int columnCount = metadata.getColumnCount();
	    for (int i = 1; i <= columnCount; i++) {
	    	if(metadata.isAutoIncrement(i)){
	    		cols.add(metadata.getColumnName(i));
	    	}
	    }
    	return cols;
    }

    /**
     * Set SQL connection after bean has been initialized
     */
	public void afterPropertiesSet() throws Exception {
		connection = jdbcTemplate.getDataSource().getConnection();
	}
}
