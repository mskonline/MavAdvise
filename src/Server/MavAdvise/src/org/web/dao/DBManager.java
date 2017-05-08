package org.web.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.web.beans.Response;
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
	
	@SuppressWarnings("unchecked")
	public void updatePassword(String netId, String password){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where netID = :netID");

		List<User> userList = q.setParameter("netID", netId).list();

		if(userList.size() >= 1){
			User user = userList.get(0);
			//user.setDeviceID(newDeviceID);
			user.setPassword(password);
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
	public String changePassword(String netId, String oldpassword, String newPassword){
		Session session = factory.openSession();
		Query q =  session.createQuery("from User where netID = :netID");
		String msg = null;
		List<User> userList = q.setParameter("netID", netId).list();

		if(userList.size() >= 1){
			User user = userList.get(0);
			//user.setDeviceID(newDeviceID);
			if(oldpassword.equalsIgnoreCase(user.getPassword())){
				user.setPassword(newPassword);
				session.beginTransaction();
				session.update(user);
				session.getTransaction().commit();

				session.flush();
				session.close();
				msg= "Changed password successfully!!";
			}
			else{
				msg="Password doesnot match";
			}
			
		} else {
			session.close();
		}
		return msg;
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

	@SuppressWarnings("unchecked")
	public List<User> startSession(Integer sessionID){
		Session session = factory.openSession();
		Transaction tx = null;

		// Set the session status as STARTED
		try {
			tx = session.beginTransaction();
			org.web.beans.Session advSession = session.get(org.web.beans.Session.class, sessionID);
			advSession.setStatus("STARTED");

			session.update(advSession);
			session.flush();

			tx.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());

			tx.rollback();
			session.close();

			return new ArrayList<User>();
		}

		// Set the first appointment as ON GOING
		Query appointmentQ = session.createQuery("from Appointment where sessionID=:sessionID and slotNo = 1");
		Appointment appointment = (Appointment) appointmentQ.setParameter("sessionID", sessionID).uniqueResult();

		try {
			tx = session.beginTransaction();
			appointment.setStatus("ON GOING");

			session.update(appointment);
			session.flush();

			tx.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());

			tx.rollback();
			session.close();

			return new ArrayList<User>();
		}

		// Return all the Users of the session for notifying them
		Query usersQ =  session.createQuery("from User where netID in (select netID from Appointment where sessionID=:sessionID)");
		List<User> users = usersQ.setParameter("sessionID", sessionID).list();

		session.close();
		return users;
	}


	@SuppressWarnings("unchecked")
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
		Session session = factory.openSession();
		Transaction tx = null;

		// Set the appointment status as NO SHOW
		try {
			tx = session.beginTransaction();
			Appointment appointment = session.get(Appointment.class, appointmentID);
			appointment.setStatus("NO SHOW");

			session.update(appointment);
			session.flush();

			tx.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());

			tx.rollback();
			session.close();

			return false;
		}

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


	@SuppressWarnings("unused")
	public Response createAppointment(int sessID, String netID, Date date){

		Session session = factory.openSession();
		List<Integer> sessIds= new ArrayList<Integer>();
		int i=0;
		Response r = new Response();
		int counter =0;
	
		org.web.beans.Appointment appointment = null;
		List<org.web.beans.Session> SessionData = new ArrayList<org.web.beans.Session>();
		List<Object> allAppointments = null;

		System.out.println("here2");

		//TODO Synchronise the block

		Criteria criteria = session.createCriteria(org.web.beans.Session.class);
		criteria.add(Restrictions.eq("sessionID", sessID));
		SessionData = criteria.list();

		//SessionData = (org.web.beans.Session)SessionData1;

		System.out.println("here3");
		
//		List<org.web.beans.Appointment>AppointmentsNoShow = null;
//		Criteria criteriaAppNo = session.createCriteria(org.web.beans.Appointment.class);
//		criteriaAppNo.add(Restrictions.eq("netID", netID));
//		criteriaAppNo.add(Restrictions.between("date", adddate(now(),-7), now()));
//		AppointmentsNoShow = criteriaAppNo.list();
		
		List<Object> res = null;
		
		SQLQuery q = (SQLQuery) session.getNamedQuery("getNoShowSessions").setString("netID", netID);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
	    res = q.list();

	    System.out.println(res.toString());
	    
		if(res==null|| res.size()==0){
			System.out.println("No no shows");
						
		}else{
			System.out.println("No show");
			r.setMessage("You have a no-show ban.");
			return r;
		}

		List<org.web.beans.Appointment>Appointments = null;
		Criteria criteriaApp = session.createCriteria(org.web.beans.Appointment.class);
		criteriaApp.add(Restrictions.eq("netID", netID));
		Appointments = criteriaApp.list();
		

		if(Appointments==null){
			System.out.println("Appointments is zero");
		}
		
//		for(org.web.beans.Appointment appNoShow : Appointments){
//			if(appNoShow.getStatus() == "NOSHOW"){
//				if((appNoShow.getDate().compareTo(dateToday))>-7){
//					System.out.println("There is no show " + appNoShow.getDate().toString());
//					
//				}
//			}
//			
//		}

		org.web.beans.Session sess3 = new org.web.beans.Session();
		int flag =0;

		for(Object sess2: SessionData){
			sess3 = (org.web.beans.Session) sess2;
			System.out.println(sess3.getStatus());
		for(org.web.beans.Appointment app2 : Appointments){
			System.out.println(app2.getNetID());
			if(app2!=null){
				System.out.println(app2.getSessionID());
					sessIds.add(app2.getSessionID());
					System.out.println(sessIds.get(0));
					flag=1;
					i++;

			}
		}
		}


		System.out.println("here4");

		if((flag==1)&&(sessIds!=null)){
			List<org.web.beans.Session> SessionConflicts = null;
		Criteria criteriaConf = session.createCriteria(org.web.beans.Session.class);
		criteriaConf.add(Restrictions.in("sessionID", sessIds));
		SessionConflicts = criteriaConf.list();
		flag=0;

		System.out.println("here5");

			if(sess3!=null){
				for(org.web.beans.Session sessioncon : SessionConflicts){
					if(sessioncon!=null){

						if(sess3.getDate() == sessioncon.getDate()){
							if((sess3.getStartTime().before(sessioncon.getEndTime())
							   &&sessioncon.getStartTime().before(sess3.getStartTime()))
							   || (sessioncon.getStartTime().before(sess3.getEndTime())
							   && sess3.getEndTime().before(sessioncon.getEndTime()))
							   || (sess3.getStartTime().equals(sessioncon.getStartTime()))){

								flag=1;
								r.setMessage("Appointment conflicts with your other appointments.");
								return r;

							}
						}
					}
				}


			if(sess3.getSlotCounter()< sess3.getNoOfSlots()){

				appointment = new org.web.beans.Appointment();
				appointment.setNetID(netID);
				appointment.setSessionID(sessID);
				appointment.setDate(date);
				appointment.setStatus("SCHEDULED");
				appointment.setSlotNo(sess3.getSlotCounter()+1);

				Transaction tx = null;
				Session hSession = factory.openSession();

	        	try{
	    			tx = hSession.beginTransaction();

	    			hSession.save(appointment);

	    			hSession.flush();
	    			hSession.clear();
	    			tx.commit();

	    			hSession.close();


	    		} catch(Exception e){
	    			tx.rollback();
	    			hSession.close();

	    			logger.error("Error creating the Appointment. " + e.getMessage());
	    			return null;
	    		}


	        	Session nsession = factory.openSession();
	        	Transaction txn = nsession.beginTransaction();

	        	counter = sess3.getSlotCounter() + 1;
	        	String hqlUpdate = "update Session  set slotCounter = :counter where sessionID = :sessionid";
	        	// or String hqlUpdate = "update Customer set name = :newName where name = :oldName";
	        	int updatedEntities = nsession.createQuery( hqlUpdate )
	        	        .setParameter("counter", counter )
	        	        .setParameter("sessionid", sess3.getSessionID() )
	        	        .executeUpdate();
	        	txn.commit();
	        	nsession.close();

	        	allAppointments = getAppointments(netID);

	        	r.setMessage("Success");
				r.setResult(allAppointments);
				return r;

			}

			}else{
				r.setMessage("Session not found");
				return r;

				}
		}


		if((flag==0)&&(sess3.getSlotCounter()< sess3.getNoOfSlots())){

			appointment = new org.web.beans.Appointment();
			appointment.setNetID(netID);
			appointment.setSessionID(sessID);
			appointment.setDate(date);
			appointment.setStatus("SCHEDULED");
			appointment.setSlotNo(sess3.getSlotCounter()+1);

			Transaction tx = null;
			Session hSession = factory.openSession();

			//TODO increment the slot counter in session table
        	try{
    			tx = hSession.beginTransaction();

    			hSession.save(appointment);

    			hSession.flush();
    			hSession.clear();
    			tx.commit();

    			hSession.close();
    		} catch(Exception e){
    			tx.rollback();
    			hSession.close();

    			logger.error("Error creating the Appointment. " + e.getMessage());
    			return null;
    		}


			Session usession = factory.openSession();
        	Transaction txn = usession.beginTransaction();

        	counter = sess3.getSlotCounter() + 1;
        	String hqlUpdate = "update sessions  set slot_counter = :counter where session_id = :sessionid";
        	// or String hqlUpdate = "update Customer set name = :newName where name = :oldName";
        	int updatedEntities = usession.createQuery( hqlUpdate )
        	        .setParameter("counter", counter )
        	        .setParameter("sessionid", sess3.getSessionID() )
        	        .executeUpdate();
        	txn.commit();
        	usession.close();
        	allAppointments = getAppointments(netID);

			r.setMessage("Success");
			r.setResult(allAppointments);
			return r;

			}else{
				r.setMessage("Slots for this appointment are over.");
				return r;
			}

			//return r;

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
		Session nsession = factory.openSession();
		Transaction tx = null;
		Transaction tx1 = null;
		List<Object> allAppointments = null;
		
		if(appointmentIDs==null){
			System.out.println("null appointemnts");
		}else{
			System.out.println("hasvalues in appointment");
		}
		
		for (Integer object : appointmentIDs) {
			System.out.println("" + object.intValue());
		}

		try {
			tx = session.beginTransaction();
			
			//String stat = "CANCELLED";

			Query q =  session.createQuery("update Appointment set status = :stat where netID = :netID and sessionID in (:appointmentIDs)");
			q.setParameter("stat", "CANCELLED");
			q.setParameter("netID", netID);
			q.setParameterList("appointmentIDs", appointmentIDs);

			int result = q.executeUpdate();
			tx.commit();

			session.close();
           	
            if(result > 0){
				allAppointments = getAppointments(netID);
            }
            else{
            	System.out.println("couldn't delete");
            }
            

		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();
		}
		
		
		try{
			 tx1 = nsession.beginTransaction();
				
				Query q1 =  nsession.createQuery("update Session set slotCounter = slotCounter-1 where sessionID in (:appointmentIDs)");
				q1.setParameterList("appointmentIDs", appointmentIDs);

				int result1 = q1.executeUpdate();
				tx1.commit();

				nsession.close();
				
				if(result1==0){
	            	System.out.println("couldn't update counter");
	            }
	            
				
		}catch (Exception e) {
			
			tx1.rollback();

	if(nsession.isOpen())
		nsession.close();

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

		//TODO: Cancel all appointments

		return msg;
	}

	public String markSessionAsDone(Integer sessionID){
		String msg = "Session marked as DONE";

		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			org.web.beans.Session advSession = session.get(org.web.beans.Session.class, sessionID);
			advSession.setStatus("DONE");

			session.update(advSession);
			session.flush();

			tx.commit();
			session.close();
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();

			msg = "Session : Failed to finish. Try again later.";
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
	public List<Object> getAllAnnouncements(String startDate, String endDate, String branch, String netID){
		Session session = factory.openSession();
		
		StringBuilder stringBuilder = new StringBuilder();
		if(StringUtils.isNotBlank(netID))
		{
			stringBuilder.append("announcements.net_id = \"");
			stringBuilder.append(netID);
			
			stringBuilder.append("\" and ");
		}
		
		if(!branch.equalsIgnoreCase("ALL")){
		
		stringBuilder.append("users.branch = \"");
		stringBuilder.append(branch);
		stringBuilder.append("\" and ");
		}



		String sql = session.getNamedQuery("getAllAnnouncements").getQueryString();
		sql=sql.replace("#BRANCH_CONDITION#",stringBuilder.toString());
		SQLQuery q = (SQLQuery) session.createSQLQuery(sql).setString("startDate", startDate).setString("endDate",endDate);
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		List<Object> allAnnouncements = q.list();

		session.close();
		return allAnnouncements;
	}
	
	public String deleteAnnouncement(int announcementID){
		Session session = factory.openSession();
		Transaction tx = null;
		//List<Object> allAnnouncement = null;
		String msg = null;

		try {
			tx = session.beginTransaction();

			Query q =  session.createQuery("delete Announcement where announcementID = :a_id");
			q.setParameter("a_id", announcementID);

			int result = q.executeUpdate();
			tx.commit();

			session.close();
			
		} catch (Exception e) {
			tx.rollback();

			if(session.isOpen())
				session.close();

			e.printStackTrace();
			msg = "Error in deleting announcement";
		}
		
		return msg;
	
	}

}
