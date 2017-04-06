package org.web.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import org.web.beans.User;

@Component
public class DBManager {

	final static Logger logger = Logger.getLogger(DBManager.class);
	private static SessionFactory factory;

	public DBManager() {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			logger.error("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public String saveUser(User user){
		String msg = null;

		try{
			Transaction tx = null;
			Session session = factory.openSession();
			tx = session.beginTransaction();

			session.save(user);
			tx.commit();

			session.close();
		} catch(Exception e){
			logger.error("Error saving the user details. " + e.getMessage());

			// Check for any constraint violations
			if(doesNetIDExists(user.getNetID()))
				msg = "Net ID already exists";

			if(doesUTAIDExists(user.getUtaID()))
				msg = "UTA ID already exists";

			if(doesEmailExists(user.getEmail()))
				msg = "Email already exists";

			// Other fatal DB error - Needs debugging
			msg = "There was some error during registeration. Please try later.";
		}

		return msg;
	}

	@SuppressWarnings("unchecked")
	public User getUser(String netID){
		Session session = factory.openSession();
		Query<User> q =  session.createQuery("from User where net_id = :netID");

		List<User> userList = q.setParameter("netID", netID).list();

		if(userList.size() >= 1)
			return userList.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private boolean doesNetIDExists(String netID){
		Session session = factory.openSession();
		Query<User> q =  session.createQuery("from User where net_id = :netID");

		List<User> userList = q.setParameter("netID", netID).list();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	private boolean doesUTAIDExists(String UTAID){
		Session session = factory.openSession();
		Query<User> q =  session.createQuery("from User where uta_id = :UTAID");

		List<User> userList = q.setParameter("UTAID", UTAID).list();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	private boolean doesEmailExists(String email){
		Session session = factory.openSession();
		Query<User> q =  session.createQuery("from User where email = :Email");

		List<User> userList = q.setParameter("Email", email).list();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	public void addSessions(org.web.beans.Session sessionInfo){
		DateTime start = DateTime.parse(sessionInfo.getStartDate().toString());
		DateTime end = DateTime.parse(sessionInfo.getEndDate().toString());
		DateTime tmp = start;

		int[] dayOfWeek = new int[8];
		java.sql.Date d;
		org.web.beans.Session session = null;
		List<org.web.beans.Session> sessions = new ArrayList<org.web.beans.Session>();

		for(int i = 0; i < sessionInfo.getDaysOfWeek().length(); ++i){
			int index = Character.getNumericValue(sessionInfo.getDaysOfWeek().charAt(i));
			dayOfWeek[index] = 1;
		}

        while(!tmp.isAfter(end)) {
        	if(dayOfWeek[tmp.getDayOfWeek()] == 1){
        		session = new org.web.beans.Session();
        		session.setNetID(sessionInfo.getNetID());

        		d = new java.sql.Date(tmp.getMillis());
        		session.setDate(d);

        		session.setStartTime(sessionInfo.getStartTime());
        		session.setEndTime(sessionInfo.getEndTime());
        		session.setNoOfSlots(sessionInfo.getNoOfSlots());
        		session.setStatus("SCHEDULED");

        		sessions.add(session);
        	}

            tmp = tmp.plusDays(1);
        }

        if(sessions.size() != 0){
        	try{
    			Transaction tx = null;
    			Session hSession = factory.openSession();
    			tx = hSession.beginTransaction();

    			for(org.web.beans.Session s : sessions)
    				hSession.save(s);

    			tx.commit();

    			hSession.close();
    		} catch(Exception e){
    			logger.error("Error saving the sessions. " + e.getMessage());
    		}
        }
	}
}
