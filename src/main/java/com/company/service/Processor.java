package com.company.service;
public interface Processor {
	/**
	 * Process events
	 * @param filePath - event json data file path
	 * @param threshold - the threshold in ms to make an event to be an alert
	 * @return - the number of alert events
	 */
	int processFile(String filePath, int threshold);
}