package com.company.persistence;

import java.sql.Connection;

public interface DatabaseProvider {
	Connection getConnection();
}
