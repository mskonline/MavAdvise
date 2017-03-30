package org.web.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
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

	public boolean saveUser(User user){
		try{
			Transaction tx = null;
			Session session = factory.openSession();
			tx = session.beginTransaction();

			session.save(user);
			tx.commit();

			session.close();
		} catch(Exception e){
			logger.error("Error saving the user details. " + e.getMessage());
			return false;
		}

		return true;
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public User getUser(String netID){
		Session session = factory.openSession();
		Query q = session.createQuery("from User where net_id = :netID");

		@SuppressWarnings("unchecked")
		List<User> userList = q.setParameter("netID", netID).list();

		if(userList.size() >= 1)
			return userList.get(0);
		else
			return null;
	}
}
