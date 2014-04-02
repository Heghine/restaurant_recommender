package com.restaurant.recommender.utils;

public class Utils {
	public static final String TYPE_RESTAURANT = "RESTAURANT";
	public static final String TYPE_CAFE = "CAFE";
	public static final String TYPE_BAR = "BAR";
	public static final String TYPE_ART = "ART";
	public static final String TYPE_ENTERTAINMENT = "ENTERTAINMENT";
	
	public static boolean isPageTypeRestaurant(String type) {
		if (type.contains(TYPE_RESTAURANT) || type.contains(TYPE_CAFE) || type.contains(TYPE_BAR)
				|| (type.contains(TYPE_ART) && type.contains(TYPE_ENTERTAINMENT))) {
			return true;
		}
		
		return false;
	}
	
}
