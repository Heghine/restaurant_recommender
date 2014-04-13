package com.restaurant.recommender.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	public static final String TYPE_RESTAURANT = "RESTAURANT";
	public static final String TYPE_CAFE = "CAFE";
	public static final String TYPE_BAR = "BAR";
	public static final String TYPE_ART = "ART";
	public static final String TYPE_ENTERTAINMENT = "ENTERTAINMENT";
	
	public static final String GENDER_MALE = "male";
	public static final String GENDER_FEMALE = "female";
	
	public static boolean isPageTypeRestaurant(String type) {
		if (type.contains(TYPE_RESTAURANT) || type.contains(TYPE_CAFE) || type.contains(TYPE_BAR)
				|| (type.contains(TYPE_ART) && type.contains(TYPE_ENTERTAINMENT))) {
			return true;
		}
		
		return false;
	}
	
	public static int getGenderCode(String gender) {
		if (gender.equals(GENDER_MALE)) {
			return 0;
		} else if (gender.equals(GENDER_FEMALE)) {
			return 1;
		}
		return 0;
	}
	
	
	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest m = MessageDigest.getInstance("MD5");

			m.reset();
			m.update(s.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hexString = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hexString.length() < 32) {
				hexString = "0" + hexString;
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
