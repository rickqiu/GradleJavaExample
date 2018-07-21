package com.company.persistence;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.company.dto.Event;

public class DatabaseUtilTest {
	private String log4jConfigurationFile = "";
	
	@Before
	public void setUp() throws Exception {
		File f = new File("src/main/resources/log4j2.xml");
		log4jConfigurationFile = System.getProperty("log4j.configurationFile", "");
		System.setProperty("log4j.configurationFile", f.getAbsolutePath());
	}

	@After
	public void tearDown() throws Exception {
		System.setProperty("log4j.configurationFile", log4jConfigurationFile);
	}


	@Test
	public void getSqlForInsertEvents() {
		String expected1 = 
		    "INSERT INTO alert_events (user_id, state, type, host, timestamp, alert) VALUES ('abc','STARTED','APP_EVENT',NULL,1491377495217,1)";
		String expected2 = 
			"INSERT INTO alert_events (user_id, state, type, host, timestamp, alert) VALUES ('abc','FINISHED',NULL,'1234',1491377495218,0)";
		
		Event evt1 = new Event();
		evt1.setId("abc");
		evt1.setState("STARTED");
		evt1.setType("APP_EVENT");
		evt1.setTimestamp(1491377495217L);
		evt1.setAlert(true);
		
		Event evt2 = new Event();
		evt2.setId("abc");
		evt2.setState("FINISHED");
		evt2.setHost("1234");
		evt2.setTimestamp(1491377495218L);

		
		List<Event> list = new ArrayList<Event>();
		list.add(evt1);
		list.add(evt2);
		
		List<String> sqlList = DatabaseUtil.getSqlForInsertEvents(list);
		assertEquals(sqlList.get(0), expected1);
		assertEquals(sqlList.get(1), expected2);		
	}
}