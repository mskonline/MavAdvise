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

        List<Object> allSessions = getSessions(sessionInfo.getNetID());

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
	public List<Object> getSessions(String netID){
		Session session = factory.openSession();

		SQLQuery q = (SQLQuery) session.getNamedQuery("getAllSessions").setString("netID", netID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allSessions = q.list();

		session.close();
		return allSessions;
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
				allSessions = getSessions(netID);
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();
		}
		return allSessions;
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
