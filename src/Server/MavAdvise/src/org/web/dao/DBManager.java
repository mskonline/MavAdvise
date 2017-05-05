package org.web.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.web.beans.Announcement;
import org.web.beans.Appointment;
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

			if(doesEmailExists(user.getEmail())){
				msg = "Email already exists";
				return msg;
			}

			if(doesNetIDExists(user.getNetID())){
				msg = "Net ID already exists";
				return msg;
			}

			if(doesUTAIDExists(user.getUtaID())){
				msg = "UTA ID already exists";
				return msg;
			}

			// Other fatal DB error - Needs debugging
			if(msg == null)
				msg = "There was some error during registeration. Please try later.";
		}

		return msg;
	}

	@SuppressWarnings("unchecked")
	public User getUser(String netID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where net_id = :netID");

		List<User> userList = q.setParameter("netID", netID).list();

		session.close();

		if(userList.size() >= 1)
			return userList.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private boolean doesNetIDExists(String netID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where net_id = :netID");

		List<User> userList = q.setParameter("netID", netID).list();

		session.close();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	private boolean doesUTAIDExists(String UTAID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where uta_id = :UTAID");

		List<User> userList = q.setParameter("UTAID", UTAID).list();

		session.close();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	private boolean doesEmailExists(String email){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where email = :Email");

		List<User> userList = q.setParameter("Email", email).list();

		session.close();

		if(userList.size() >= 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	public void updateUserDeviceID(String netID, String newDeviceID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where netID = :netID");

		List<User> userList = q.setParameter("netID", netID).list();

		if(userList.size() >= 1){
			User user = userList.get(0);
			user.setDeviceID(newDeviceID);

			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();

			session.flush();
			session.close();
		} else {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void updateDeviceID(String newDeviceID, String oldDeviceID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where deviceID = :deviceID");

		List<User> userList = q.setParameter("deviceID", oldDeviceID).list();

		if(userList.size() >= 1){
			User user = userList.get(0);
			user.setDeviceID(newDeviceID);

			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();

			session.flush();
			session.close();
		} else {
			session.close();
		}
	}

	public Map<String, List<Object>> addSessions(org.web.beans.SessionInfo sessionInfo){
		DateTime start = DateTime.parse(sessionInfo.getStartDate().toString());
		DateTime end = DateTime.parse(sessionInfo.getEndDate().toString());
		DateTime tmp = start;

		int[] dayOfWeek = new int[8];
		Date d;
		org.web.beans.Session session = null;

		Map<String, List<Object>> sessions = new HashMap<String, List<Object>>();
		List<org.web.beans.Session> addSessions = new ArrayList<org.web.beans.Session>();
		List<Object> conflictingSessions = new ArrayList<Object>();

		List<org.web.beans.Session> existingSessions =
				getAllScheduledSessions(sessionInfo.getNetID(),
										sessionInfo.getStartDate(),
										sessionInfo.getEndDate());

		for(int i = 0; i < sessionInfo.getFrequency().length(); ++i)
			dayOfWeek[i + 1] = Character.getNumericValue(sessionInfo.getFrequency().charAt(i));

        while(!tmp.isAfter(end)) {
        	if(dayOfWeek[tmp.getDayOfWeek()] == 1){
        		session = new org.web.beans.Session();
        		session.setNetID(sessionInfo.getNetID());

        		d = new Date(tmp.getMillis());
        		session.setDate(d);
        		session.setStartTime(sessionInfo.getStartTime());
        		session.setEndTime(sessionInfo.getEndTime());
        		session.setNoOfSlots(sessionInfo.getNoOfSlots());
        		session.setSlotCounter(0);
        		session.setStatus("SCHEDULED");
        		session.setLocation(sessionInfo.getLocation());

        		if(existingSessions == null)
        			addSessions.add(session);
        		else{
        			if(existingSessions.contains(session))
        				conflictingSessions.add(session);
        			else
        				addSessions.add(session);
        		}
        	}

            tmp = tmp.plusDays(1);
        }

        if(addSessions.size() != 0){
        	Transaction tx = null;
			Session hSession = factory.openSession();

        	try{
    			tx = hSession.beginTransaction();

    			for(org.web.beans.Session s : addSessions)
    				hSession.save(s);

    			hSession.flush();
    			hSession.clear();
    			tx.commit();

    			hSession.close();
    		} catch(Exception e){
    			tx.rollback();
    			hSession.close();

    			logger.error("Error saving the sessions. " + e.getMessage());
    			return null;
    		}
        }

        List<Object> allSessions = getAllSessions(sessionInfo.getNetID());

        sessions.put("allSessions", allSessions);
        sessions.put("conflictingSessions", conflictingSessions);

        return sessions;
	}

	@SuppressWarnings("unchecked")
	public List<org.web.beans.Session> getAllScheduledSessions(String netID, Date startDate, Date endDate){
		Session session = factory.openSession();
		List<org.web.beans.Session> allSessions = null;

		Criteria criteria = session.createCriteria(org.web.beans.Session.class);
		criteria.addOrder(Order.asc("date"));
		criteria.add(Restrictions.eq("netID", netID));
		criteria.add(Restrictions.eq("status", "SCHEDULED"));

		if(startDate != null && endDate != null)
			criteria.add(Restrictions.between("date", startDate, endDate));

		allSessions = criteria.list();

		session.close();
		return allSessions;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getAllSessions(String netID){
		Session session = factory.openSession();

		SQLQuery q = (SQLQuery) session.getNamedQuery("getAllSessions").setString("netID", netID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allSessions = q.list();

		session.close();
		return allSessions;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getScheduledSessionsUpto(String netID, String date){
		Session session = factory.openSession();

		SQLQuery q = (SQLQuery) session.getNamedQuery("getSCHDSessionsUpto")
								.setString("netID", netID)
								.setString("date", date);

		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allSessions = q.list();

		session.close();
		return allSessions;
	}

	public List<User> getUsersForSession(Integer sessionID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where netID in (select netID from Appointment where sessionID=:sessionID)");

		List<User> users = q.setParameter("sessionID", sessionID).list();

		session.close();
		return users;
	}

	public User getUserForAppointment(Integer appointmentID){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where netID in (select netID from Appointment where appointmentID=:appointmentID)");

		List<User> users = q.setParameter("appointmentID", appointmentID).list();

		session.close();

		if(users != null && users.size() > 0)
			return users.get(0);
		else
			return null;

	}

	public void markAppointmentAsDone(Integer appointmentID){
		Session session = factory.openSession();
		Appointment appointment = (Appointment) session.get(Appointment.class, appointmentID);

		try{
			appointment.setStatus("DONE");

			session.beginTransaction();
			session.update(appointment);
			session.getTransaction().commit();

			session.flush();
			session.close();
		} catch(Exception e){
			logger.error("" + e.getMessage());

			session.getTransaction().rollback();
			session.close();
		}
	}

	public boolean markAppointmentAsNoShow(Integer appointmentID){

		return true;
	}


	@SuppressWarnings("unchecked")
	public List<Object> getAppointments(String netID){
		Session session = factory.openSession();

		SQLQuery q = (SQLQuery) session.getNamedQuery("getAllAppointments").setString("netID", netID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allSessions = q.list();

		session.close();
		return allSessions;
	}


	public List<org.web.beans.User> getAdvisors(String branch){
		Session session = factory.openSession();
		List<org.web.beans.User> allAdvisors = null;

		Criteria criteria = session.createCriteria(org.web.beans.User.class);
		criteria.addOrder(Order.asc("firstName"));
		criteria.add(Restrictions.eq("branch", branch));
		criteria.add(Restrictions.eq("roleType", "Advisor"));


		allAdvisors = criteria.list();

		session.close();
		return allAdvisors;


	}


	public List<Object> getSessionDates(String netID){
		Session session = factory.openSession();

		System.out.println("Inside Dates");
		SQLQuery q = (SQLQuery) session.getNamedQuery("getDateSessions").setString("netID", netID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		System.out.println("Inside Dates1");
		List<Object> allSessions = q.list();

		session.close();
		return allSessions;
	}


	public List<Object> deleteAppointments(String netID, Integer[] appointmentIDs){
		Session session = factory.openSession();
		Transaction tx = null;
		List<Object> allAppointments = null;

		try {
			tx = session.beginTransaction();

			Query q =  session.createQuery("update Appointment set status = \"CANCELLED\" where netID = :netID and appointmentID in (:appointmentIDs)");
			q.setParameter("netID", netID);
			q.setParameterList("appointmentIDs", appointmentIDs);

			int result = q.executeUpdate();
			tx.commit();

			session.close();

			if(result > 0)
				allAppointments = getAllSessions(netID);
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();
		}
		return allAppointments;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getSessionAppointments(Integer sessionID){
		Session session = factory.openSession();

		SQLQuery q = (SQLQuery) session.getNamedQuery("getAllSessionAppointments")
				.setInteger("sessionID", sessionID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allAppointments = q.list();

		session.close();
		return allAppointments;
	}

	public List<Object> deleteSessions(String netID, Integer[] sessionIDs){
		Session session = factory.openSession();
		Transaction tx = null;
		List<Object> allSessions = null;

		try {
			tx = session.beginTransaction();

			Query q =  session.createQuery("delete Session where netID = :netID and sessionID in (:sessionIDs)");
			q.setParameter("netID", netID);
			q.setParameterList("sessionIDs", sessionIDs);

			int result = q.executeUpdate();
			tx.commit();

			session.close();

			if(result > 0)
				allSessions = getAllSessions(netID);
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();
		}
		return allSessions;
	}

	public String cancelSession(Integer sessionID, String cancelReason){
		String msg = null;

		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			org.web.beans.Session advSession = session.get(org.web.beans.Session.class, sessionID);
			advSession.setStatus("CANCELLED");
			advSession.setComment(cancelReason);

			session.update(advSession);
			session.flush();

			tx.commit();
			session.close();
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();

			msg = "Failed to cancel this session. Try again later.";

		}

		return msg;
	}

	public String saveAnnouncement(Announcement announ){
		String msg = null;

		try{
			Transaction tx = null;
			Session session = factory.openSession();
			tx = session.beginTransaction();

			session.save(announ);
			tx.commit();

			session.close();
		} catch(Exception e){
			logger.error("Error saving the announcement details. " + e.getMessage());

			// Other fatal DB error - Needs debugging
			if(msg == null)
				msg = "There was some error during registeration. Please try later.";

		}

		return msg;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getAllAnnouncements(String startDate, String endDate, String branch){
		Session session = factory.openSession();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("users.branch = \"");
		stringBuilder.append(branch);
		stringBuilder.append("\" and");

		String sql = session.getNamedQuery("getAllAnnouncements").getQueryString();

		if(branch.equalsIgnoreCase("ALL"))
			//q.setString("BRANCH_CONDITION","");
			sql=sql.replace("#BRANCH_CONDITION#", "");
		else
			sql=sql.replace("#BRANCH_CONDITION#",stringBuilder.toString());

		SQLQuery q = (SQLQuery) session.createSQLQuery(sql).setString("startDate", startDate).setString("endDate",endDate);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allAnnouncements = q.list();

		session.close();
		return allAnnouncements;
	}
}
