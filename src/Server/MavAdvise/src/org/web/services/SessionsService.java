package org.web.services;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.web.beans.SessionInfo;

@Service
public class SessionsService {

	public void adjustSessionDates(SessionInfo sessionInfo){
		if(sessionInfo.getStartDate().equals(sessionInfo.getEndDate()))
			return;

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
		for(int i = 0; i < sessionInfo.getFrequency().length(); ++i)
			dayOfWeek[i + 1] = Character.getNumericValue(sessionInfo.getFrequency().charAt(i));

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
}
