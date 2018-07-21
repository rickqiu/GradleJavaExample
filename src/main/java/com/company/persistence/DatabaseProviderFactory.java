package com.company.persistence;

public class DatabaseProviderFactory {
	
	public static DatabaseProvider getDatabaseProvider(String dbType, String mode, String dbFile) {
		
		if (dbType == null || dbType.isEmpty()) {
			return null;
		} else if (dbType.equals("HSQL")) {

			DatabaseProvider p = new HSqlDatabaseProvider(mode, dbFile);
			return p;
		}

		return null;
	}
}
