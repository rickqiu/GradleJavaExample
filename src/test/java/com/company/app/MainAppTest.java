package com.company.app;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainAppTest {
	private String log4jConfigurationFile = "";
	
	@Before
	public void setUp() throws Exception {
		File f = new File("src/main/resources/log4j2.xml");
		log4jConfigurationFile = System.getProperty("log4j.configurationFile", "");
		System.setProperty("log4j.configurationFile", f.getAbsolutePath());
		MainApp.createDatabaseSchema("file", "test_db");
	}

	@After
	public void tearDown() throws Exception {
		MainApp.shutdownDatabase("file", "test_db");
		System.setProperty("log4j.configurationFile", log4jConfigurationFile);
	}

	@Test
	public void execute() {
		int expected = 4;
		int actual = MainApp.execute("in/samples.json", 4, "file", "test_db");
		assertTrue(actual == expected);	
	}
}