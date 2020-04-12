package org.web.beans;

import java.sql.Date;
import java.sql.Time;

/**
 * Bean representing the Sessions table 
 * 
 * @author mskonline
 */

public class Session {
	private int sessionID;
	private String netID;
	private Date date;
	private Time startTime;
	private Time endTime;
	private int noOfSlots;
	private int slotCounter;
	private String status;
	private String comment;
	private String location;

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getSlotCounter() {
		return slotCounter;
	}

	public void setSlotCounter(int slotCounter) {
		this.slotCounter = slotCounter;
	}

	public String getNetID() {
		return netID;
	}

	public void setNetID(String netID) {
		this.netID = netID;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && obj instanceof Session) {
			if (date.equals(((Session) obj).date) && startTime.before(((Session) obj).endTime))
				isEqual = true;
		}

		return isEqual;
	}
}
