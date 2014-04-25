package com.restaurant.recommender.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import com.restaurant.recommender.data.FacebookPageData;
import com.restaurant.recommender.manager.UserDataManager;

public class Utils {
	public static final String TYPE_RESTAURANT = "RESTAURANT";
	public static final String TYPE_CAFE = "CAFE";
	public static final String TYPE_BAR = "BAR";
	public static final String TYPE_PUB = "PUB";
	public static final String TYPE_CLUB = "CLUB";
	public static final String TYPE_ART = "ART";
	public static final String TYPE_ENTERTAINMENT = "ENTERTAINMENT";
	
	public static final String GENDER_MALE = "male";
	public static final String GENDER_FEMALE = "female";
	
	@SuppressLint("DefaultLocale")
	public static boolean isPageTypeRestaurant(String type) {
		type = type.toUpperCase();
		if (type.contains(TYPE_RESTAURANT) || type.contains(TYPE_CAFE) || type.contains(TYPE_BAR)
				|| type.contains(TYPE_PUB) || type.contains(TYPE_CLUB) 
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
	
	public static String getFbPageIdsString() {
		HashMap<String, FacebookPageData> userPages = UserDataManager.$().userRestaurantPages;
		
		String userPagesStr = "";
		for (String pageId : userPages.keySet()) {
			userPagesStr += pageId + ",";
		}
		userPagesStr = userPagesStr.substring(0, userPagesStr.length() - 1);
		return userPagesStr;
	}
	
	public static String getUserPreferenceString() throws JSONException {
		HashMap<String, FacebookPageData> userPages = UserDataManager.$().userRestaurantPages;
		
		String userPreferencesStr = "";
		JSONArray userPreferencesJson = new JSONArray();
		JSONObject userPreferenceJson;
		for (String pageId : userPages.keySet()) {
			userPreferenceJson = new JSONObject();
			userPreferenceJson.put("fb_id", pageId);
			userPreferenceJson.put("type", userPages.get(pageId).type);
			userPreferenceJson.put("name", userPages.get(pageId).name);
			userPreferenceJson.put("location", userPages.get(pageId).location);
			userPreferenceJson.put("working_hours", userPages.get(pageId).workingHours);
			
			userPreferencesJson.put(userPreferenceJson);
		}
		userPreferencesStr = userPreferencesJson.toString();
		
		return userPreferencesStr;
	}
	
	public static String getRatingCountString(int count) {
		String countStr = "";
		if (count > 1) {
			countStr = count + " votes";
		} else {
			countStr = count + " vote";
		}
		
		return countStr;
	}
}
