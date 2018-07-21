package com.company.service;
import java.util.List;

import com.company.dto.Event;

public abstract class AbstractProcessor  {

	/**
	 * Get a list of alert events
	 * @return alert event list
	 */
	abstract List<Event> getAlertEvents();
	
	/**
	 * Get a uid string by the concatenation of id + _ + sate + timestamp
	 * @param id
	 * @param state
	 * @param timestamp
	 * @return uid string
	 */
	public String getUid(String id, String state, Long timestamp) {
		StringBuilder builder = new StringBuilder();
		builder.append(id).append("_").append(state).append(Long.toString(timestamp));
		return builder.toString();
	}
}