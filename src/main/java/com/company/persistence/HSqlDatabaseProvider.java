package com.company.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HSqlDatabaseProvider implements DatabaseProvider {
	Logger logger = LogManager.getLogger(HSqlDatabaseProvider.class);

	private Connection conn;

	public HSqlDatabaseProvider(String dbMode, String dbFile) {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		}

		if (dbMode != null && dbFile != null) {
			try {
				conn = DriverManager.getConnection("jdbc:hsqldb:" + dbMode + ":" + dbFile, "sa", "");

			} catch (SQLException e) {
				logger.log(Level.ERROR, e.getMessage(), e);
			}
		}
	}

	public Connection getConnection() {
		return conn;
	}
}
