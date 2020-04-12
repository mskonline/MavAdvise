package org.web.beans;

import java.sql.Date;

public class Announcement {
	private String netID;
	private Date date;
	private String message;
	private String title;
	private int priority;
	private int announcementID;

	public int getAnnouncementID() {
		return announcementID;
	}

	public void setAnnouncementID(int annoucementID) {
		this.announcementID = annoucementID;
	}

	public String getNetID() {
		return netID;
	}

	public void setNetID(String netID) {
		this.netID = netID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
