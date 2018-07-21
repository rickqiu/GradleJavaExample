package com.company.persistence;

import java.util.List;

import com.company.dto.Event;

public interface DatabaseOperation {

	int insertEvents(List<Event> events);

	void createDabaseSchema();

	void shutdownDatabase();
}
