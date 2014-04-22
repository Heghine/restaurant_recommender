package com.restaurant.recommender.data;

import org.json.JSONObject;

public class ItemData {
	
	public int itemId;
	public String itemFbId;
	public String name;
	public String address;
	public String workingHours;
	
	public int ratingCount;
	public int rating;
	
	public ItemData(JSONObject itemJson) {
		itemId = itemJson.optInt("item_id", 0);
		itemFbId = itemJson.optString("item_fb_id", "");
		name = itemJson.optString("name", "");
		address = itemJson.optString("address", "");
		workingHours = itemJson.optString("working_hours", "");
		
		ratingCount = itemJson.optInt("rating_count", 1);
		rating = itemJson.optInt("rating", 5);
	} 

}
