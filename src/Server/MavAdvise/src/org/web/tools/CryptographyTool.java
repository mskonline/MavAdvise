package org.web.tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Class containing various Cryptographic algorithms
 * 
 * @author mskonline
 */

public class CryptographyTool {
	final static Logger logger = Logger.getLogger(CryptographyTool.class);
	final private static String PLAIN_TEXT_ENCODING = "UTF-8";
	
	private CryptographyTool() {
		// Disallow initialization of this class
	}
	
	/**
	 * Generate SHA-256 hash
	 * 
	 * @param plainText The plain text
	 * @return The hash
	 */
	public static String getSHA256Hash(final String plainText) {
		try {
			final byte[] data = plainText.getBytes(PLAIN_TEXT_ENCODING);
			final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			
			final byte[] digest = messageDigest.digest(data);
			
			return new String(digest);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			logger.error(e);
		}
		
		return null;
	}
	
	/**
	 * Generate MD5 hash
	 * 
	 * @param plainText The plain text
	 * @return The hash
	 */
	public static String getMD5Hash(final String plainText) {
		try {
			final byte[] data = plainText.getBytes(PLAIN_TEXT_ENCODING);
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			final byte[] digest = messageDigest.digest(data);
			
			return new String(digest);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			logger.error(e);
		}
		
		return null;
	}
	
	public static String encrypt(final String plainText) {
		return null;
	}
	
	public static Object encrypt(final Object object) {
		return null;
	}
	
	public static File encrypt(final File file) {
		return null;
	}
	
	public static String decrypt(final String plainText) {
		return null;
	}
	
	public static Object decrypt(final Object object) {
		return null;
	}
	
	public static File decrypt(final File file) {
		return null;
	}
}
