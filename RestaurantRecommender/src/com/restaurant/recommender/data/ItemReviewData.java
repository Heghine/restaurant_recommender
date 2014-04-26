package com.restaurant.recommender.data;

import org.json.JSONObject;

public class ItemReviewData {
	public int reviewId;
	public int itemId;
	public int rating;
	public String userId;
	public String userFbId;
	public String userName;
	public String itemType;
	public String reviewText;
	public String date;
	
	public ItemReviewData(JSONObject itemReviewDataJson) {
		reviewId = itemReviewDataJson.optInt("review_id", 0);
		itemId = itemReviewDataJson.optInt("item_id", 0);
		rating = itemReviewDataJson.optInt("rating", 0);
		userId = itemReviewDataJson.optString("user_id", "0");
		userFbId = itemReviewDataJson.optString("user_fb_id", "0");
		userName = itemReviewDataJson.optString("user_name", "");
		itemType = itemReviewDataJson.optString("type", "food");
		reviewText = itemReviewDataJson.optString("review_text", "");
		date = itemReviewDataJson.optString("date", "");
	}
}
