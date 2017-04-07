package org.web.beans;

import java.sql.Date;

public class Session {
	private int sessionID;
	private int siID;
	private Date date;
	private String status;
	private int slotCounter;
	private String comment;

	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	public int getSiID() {
		return siID;
	}
	public void setSiID(int siID) {
		this.siID = siID;
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
}
