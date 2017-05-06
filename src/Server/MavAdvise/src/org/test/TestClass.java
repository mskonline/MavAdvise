package org.test;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.web.beans.Session;

public class TestClass {

	public static void main(String[] args) {
		/*DateTime  t1 = DateTime.parse("2017-04-06");
		DateTime  t2 = DateTime.parse("2017-04-06");

		System.out.println(t1.isEqual(t2));*/

		dateTest();

	}


	public static void dateTest(){
		Calendar c = Calendar.getInstance();

		System.out.println(c.get(Calendar.DAY_OF_MONTH));
	}

	public static void StringTest(){
		String deviceID = "  ";

		System.out.println(StringUtils.isBlank(deviceID));
	}

	public static void calendarTest(){
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		System.out.println(isSameDay(c1, c2));
	}

	 public static boolean isSameDay(Calendar cal1, Calendar cal2) {
	        if (cal1 == null || cal2 == null) {
	            throw new IllegalArgumentException("The dates must not be null");
	        }
	        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
	                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	    }

	public static void sessionTest(){
		DateFormat timeformatter = new SimpleDateFormat("HH:mm:ss");
		DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
		Session s1 = new Session();
		Session s2 = new Session();

		try {
			s1.setDate(new Date(dateformatter.parse("2017-04-15").getTime()));
			s2.setDate(new Date(dateformatter.parse("2017-04-15").getTime()));

			s1.setStartTime(new Time(timeformatter.parse("9:00:00").getTime()));
			s1.setEndTime(new Time(timeformatter.parse("11:00:00").getTime()));

			s2.setStartTime(new Time(timeformatter.parse("9:01:00").getTime()));
			s2.setEndTime(new Time(timeformatter.parse("13:00:00").getTime()));

			List<Session> list = new ArrayList<Session>();
			list.add(s1);

			System.out.println(list.contains(s2));

		} catch (ParseException e) {
			e.printStackTrace();
		}


	}

	public static void timeTest(){
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Time t1 = null, t2 = null;
		try {
			t1 = new Time(formatter.parse("11:00:00").getTime());
			t2 = new Time(formatter.parse("11:00:00").getTime());

			if(t2.before(t1))
				System.out.println("Yes");
			else
				System.out.println("No");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static void dayOfWeek(){
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.APRIL, 9);
		System.out.println(c.get(Calendar.DAY_OF_WEEK));
	}
}
