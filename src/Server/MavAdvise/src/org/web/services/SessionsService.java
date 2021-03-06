package org.web.services;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.web.beans.Session;
import org.web.beans.SessionInfo;

/**
 * Service class for handling all the operations on advisor sessions
 * 
 * @author mskonline
 */
@Service
public class SessionsService {
	/**
	 * Adjust the session dates
	 * 
	 * @param sessionInfo The session info object
	 */
	public void adjustSessionDates(final SessionInfo sessionInfo){
		if(sessionInfo.getStartDate().equals(sessionInfo.getEndDate())) {
			return;
		}

		/**
		 *   M T W T F S S
		 * 0 0 0 0 0 0 0 0
		 */
		int[] dayOfWeek = new int[8];
		java.sql.Date d;

		/**
		 * Frequency
		 * M T W T F S S
		 * 0 0 0 0 0 0 0
		 */
		for (int i = 0; i < sessionInfo.getFrequency().length(); ++i) {
			dayOfWeek[i + 1] = Character.getNumericValue(sessionInfo.getFrequency().charAt(i));
		}

		DateTime start = DateTime.parse(sessionInfo.getStartDate().toString());
		DateTime end = DateTime.parse(sessionInfo.getEndDate().toString());
		DateTime tmp = start;

		// Adjust start date
		while(!tmp.isAfter(end)) {
			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
				start = tmp;
				break;
			}

			tmp = tmp.plusDays(1);
		}

		tmp = end;

		// Adjust end date
		while(!tmp.isBefore(start)) {
			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
				end = tmp;
				break;
			}

			tmp = tmp.minusDays(1);
		}

		d = new java.sql.Date(start.getMillis());
		sessionInfo.setStartDate(d);

		d = new java.sql.Date(end.getMillis());
		sessionInfo.setEndDate(d);
	}

	/**
	 * Construct a session start message
	 * 
	 * @param session The session object
	 * @return The message
	 */
	public String getStartSessionMessage(final Session session){
		StringBuilder message = new StringBuilder();

		message.append("Advising session on ");

		SimpleDateFormat dateformatter = new SimpleDateFormat("EEE, MMM d yyyy");
		message.append(dateformatter.format(session.getDate()));
		message.append(" ");

		SimpleDateFormat timeformatter = new SimpleDateFormat("h:mm a");
		message.append(timeformatter.format(session.getStartTime()));
		message.append(" - ");
		message.append(timeformatter.format(session.getEndTime()));

		message.append(" has started.\n\n");
		message.append("Please be present at ");
		message.append(session.getLocation());
		message.append(" for your appointment.");
		return message.toString();
	}

	/**
	 * Construct a appointment cancellation message 
	 * 
	 * @param session The session object
	 * @param cancelReason The cancellation reason
	 * @return The message
	 */
	public String getCancelAppointmentMessage(final Session session, final String cancelReason){
		StringBuilder message = new StringBuilder();

		message.append("Your Advising session on ");

		SimpleDateFormat dateformatter = new SimpleDateFormat("EEE, MMM d yyyy");
		message.append(dateformatter.format(session.getDate()));
		message.append(" ");

		SimpleDateFormat timeformatter = new SimpleDateFormat("h:mm a");
		message.append(timeformatter.format(session.getStartTime()));
		message.append(" - ");
		message.append(timeformatter.format(session.getEndTime()));

		message.append(" was Cancelled with following reason.\n\n");
		message.append(cancelReason);
		return message.toString();
	}
}
