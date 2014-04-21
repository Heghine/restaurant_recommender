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
	
	public ItemData(int id) {
		itemId = id;
		itemFbId = "437358056327869";
		name = "Owl's Cafebar";
		address = "Pushkin`s st. 41";
		workingHours = "12:00-00:00";
		
		ratingCount = 2;
		rating = 5;
	}
	
	public ItemData(JSONObject itemJson) {
		itemId = itemJson.optInt("item_id", 0);
		itemFbId = itemJson.optString("item_fb_id", "");
		name = itemJson.optString("name", "");
		address = itemJson.optString("address", "");
		workingHours = itemJson.optString("working_hours", "");
		
		ratingCount = itemJson.optInt("rating_count", 0);
		rating = itemJson.optInt("rating", 0);
	} 

}
