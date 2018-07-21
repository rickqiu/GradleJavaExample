package com.company.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.dto.Event;

public class DatabaseOperationImpl implements DatabaseOperation {
	Logger logger = LogManager.getLogger(DatabaseOperationImpl.class);
	
	private String dbMode;
	private String dbFile;
	
	public DatabaseOperationImpl(String dbMode, String dbFile) {
		this.dbMode = dbMode;
		this.dbFile = dbFile;
	}
	
	@Override
	public synchronized int insertEvents(List<Event> events) {
		
		DatabaseProvider dbProvider = DatabaseProviderFactory.getDatabaseProvider("HSQL", dbMode, dbFile);
		Connection connection = dbProvider.getConnection();

		int numOfAlertEvents = 0;
		
		List<String> queries = DatabaseUtil.getSqlForInsertEvents(events);

		Statement statement = null;

		try {
			statement = connection.createStatement();

			for (String query : queries) {
				statement.addBatch(query);
			}
			
			statement.executeBatch();

			numOfAlertEvents = queries.size();
			logger.log(Level.INFO, numOfAlertEvents + " events inserted into DB.");

		} catch (SQLException e) {
			numOfAlertEvents = 0;
			logger.log(Level.ERROR, e.getMessage(), e);
		} finally {
			DatabaseUtil.close(statement, connection);
		}
		
		return numOfAlertEvents;
	}

	@Override
	public synchronized void createDabaseSchema() {
		DatabaseProvider dbProvider = DatabaseProviderFactory.getDatabaseProvider("HSQL", dbMode, dbFile);
		Connection connection = dbProvider.getConnection();
		Statement st = null;

		try {
			st = connection.createStatement();

			int i = st.executeUpdate(DatabaseUtil.DB_SCHEMA_SQL);

			if (i == -1) {
				logger.log(Level.ERROR, " Create Database schema SQL:" + DatabaseUtil.DB_SCHEMA_SQL);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		} finally {
			DatabaseUtil.close(st, connection);
		}
	}

	@Override
	public void shutdownDatabase() {
		DatabaseProvider dbProvider = DatabaseProviderFactory.getDatabaseProvider("HSQL", dbMode, dbFile);
		Connection connection = dbProvider.getConnection();
		
		try {
		   Statement st = connection.createStatement();
		   st.execute("SHUTDOWN");
		   connection.close();
		} catch (SQLException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		}
	}
}
