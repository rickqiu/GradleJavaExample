package com.company.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.dto.Event;
import com.company.persistence.DatabaseOperation;
import com.company.persistence.DatabaseOperationImpl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * EventProcessor is for data read in, process events, find events to be alerted.
 * 
 * @author rqiu
 *
 */
public class EventProcessor extends AbstractProcessor implements Processor{

	private Map<String, Long> startMap = new HashMap<String, Long>();
	private Map<String, Long> finishMap = new HashMap<String, Long>();
	private Map<String, Event> objMap = new HashMap<String, Event>();
	private List<Event> alertList= new ArrayList<Event> ();
	
	private DatabaseOperation dbOperation;
	private Boolean isServiceTest = false;
	
	Logger logger = LogManager.getLogger(EventProcessor.class);
	
	public EventProcessor(String dbMode, String dbFile) {
		dbOperation = new DatabaseOperationImpl(dbMode, dbFile);
	}
	
	public EventProcessor(Boolean isServiceTest) {
		this.isServiceTest = isServiceTest;
	}
	

	/**
	 * Process events from a json file 
	 * @param filePath - the json file location
	 * @param threshold - duration in ms between a started event and a finished event
	 * @return - the number of alert events
	 */
	@Override
	public int processFile(String filePath, int threshold) {
		logger.log(Level.INFO, "Processing file started. filepath=" + filePath);

		// 1. Read in json file
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

		File file = new File(filePath);
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String line = in.readLine();

			while (line != null) {
				Event t = mapper.readValue(line, Event.class);
				if (t != null) {
					if ("STARTED".equals(t.getState())) {
						startMap.put(t.getId(), t.getTimestamp());
					} else if ("FINISHED".equals(t.getState())) {
						finishMap.put(t.getId(), t.getTimestamp());
					} else {
						logger.log(Level.INFO,
								"No matched values in FINISHED collection for Event Started:  " + t.toString());
					}

					String uid = getUid(t.getId(), t.getState(), t.getTimestamp());
					objMap.put(uid, t);
				}
				line = in.readLine();
			}
			closeStream(in);
		} catch (IOException e) {
			logger.log(Level.ERROR, e.getMessage(), e);
		} finally {
			closeStream(in);
		}
		
		// 2. Find events that take longer than 4ms
		if (startMap.size() > 0) {

			for (Map.Entry<String, Long> entry : startMap.entrySet()) {
				String id = entry.getKey();
				Long startTime = entry.getValue();
				Long finishTime = finishMap.get(id);
				Long duration = finishTime - startTime;
				if (duration > threshold) {
					String uidStart = id + "_" + "STARTED" + Long.toString(startTime);
					String uidFinish = id + "_" + "FINISHED" + Long.toString(finishTime);
					Event tStart = objMap.get(uidStart);
					Event tFinish = objMap.get(uidFinish);
					tStart.setAlert(true);
					tFinish.setAlert(true);
					alertList.add(tStart);
					alertList.add(tFinish);

					if (logger.isDebugEnabled()) {
						logger.log(Level.DEBUG, "Alert Event: " + tStart);
						logger.log(Level.DEBUG, "Alert Event: " + tFinish);
					}
				}
			}
		}
		
		int numOfAlerts = 0;
		
		if (!isServiceTest && alertList.size() > 0 ) {
			numOfAlerts = dbOperation.insertEvents(alertList);
		} else {
			numOfAlerts = alertList.size();
		}
		
		logger.log(Level.INFO, "File processed. filepath=" + filePath);
		
		return numOfAlerts;
	}
	
	/**
	 * Get a List of events when alert = true
	 * @return a list of events
	 */
	public List<Event> getAlertEvents() {
		return alertList;
	}
	
	private void closeStream(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
			}
		}
	}
}