package org.test;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.web.beans.Response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {

	public static void main(String[] args) {
		new ObjectMapperTest();
	}

	public ObjectMapperTest(){
		test();
	}

	public void test(){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String result = null;
		Abean bean = new Abean();
		bean.setFirstName("Jon");
		bean.setLastName("Smith");
		bean.setDate(new Date(Calendar.getInstance().getTimeInMillis()));

		r.setResult(bean);

		try {
			result = mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println(result);
	}

	class Abean{
		private String firstName;
		private String lastName;
		private Date date;

		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		@Override
		@JsonValue
		public String toString() {

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
			String strDate = sdf.format(date);

			String str = String.format("{f: '%s',"
					+ "l: '%s',d: '%s\'}", firstName, lastName, strDate);

			return str;
		}
	}
}
