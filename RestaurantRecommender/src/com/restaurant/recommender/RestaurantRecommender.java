package com.restaurant.recommender;

public class RestaurantRecommender {
	
	private static RestaurantRecommender instance;
	
	public MainActivity roActivity;
	
	public RestaurantRecommender(MainActivity mActivity) {
		instance = this;
		roActivity = mActivity;
	}

	public static RestaurantRecommender $() {
		return instance;
	}
}
