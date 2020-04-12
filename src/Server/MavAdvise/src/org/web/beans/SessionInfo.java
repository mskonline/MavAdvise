package org.web.beans;

import java.sql.Date;
import java.sql.Time;

/**
 * Bean for collecting form data of sessions
 * 
 * @author mskonline
 */

public class SessionInfo {
	private String netID;
	private Date startDate;
	private Date endDate;
	private Time startTime;
	private Time endTime;
	private int noOfSlots;
	private String frequency;
	private String location;

	public String getNetID() {
		return netID;
	}

	public void setNetID(String netID) {
		this.netID = netID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public int getNoOfSlots() {
		return noOfSlots;
	}

	public void setNoOfSlots(int noOfSlots) {
		this.noOfSlots = noOfSlots;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
