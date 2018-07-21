package com.company.service;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.company.service.EventProcessor;

public class EventProcessorTest {
	
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
	public void processFile() {
		Processor p = new EventProcessor(true);
		int result = p.processFile("in/samples.json", 4);
		assertTrue(result == 4);
	}
}
