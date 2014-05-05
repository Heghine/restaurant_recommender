package com.restaurant.recommender.utils;

public class Constants {	
	
	public static final String API_URL = "http://10.15.1.243/restaurant_recommender/api.php?";//"http://192.168.4.237/restaurant_recommender/api.php?"; //"http://192.168.4.237:8080/RestaurantRecommenderAPI";
	public static final String API_STAGE_URL = "http://192.168.4.237/restaurant_recommender_stage/api.php?";
	
	public static final String SERVER_URL = API_URL;
	
	public static final String DEFAULT_CITY = "Yerevan, Armenia";
	public static final String DEFAULT_HOURS = "10:00 - 00:00";
	public static final int FACEBOOK_PAGES_LIMIT = 1000;
	
	public static final int RECOMMENDATION_TYPE_TOPN = 1;
	public static final int RECOMMENDATION_TYPE_PREDICTION = 2;
	
	public static final String MOOD_RECOMMENDATION_TYPE_COFFEE = "coffee";
	public static final String MOOD_RECOMMENDATION_TYPE_DANCE = "dance";
	public static final String MOOD_RECOMMENDATION_TYPE_SAD = "sad";
	public static final String MOOD_RECOMMENDATION_TYPE_FOOD = "food";
	public static final String MOOD_RECOMMENDATION_TYPE_MUSIC = "music";

}