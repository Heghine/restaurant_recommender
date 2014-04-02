package com.restaurant.recommender.data;

import org.json.JSONObject;

public class FacebookPageData {
	public String pageId;
	public String type;
	public String name;
	
	public FacebookPageData(JSONObject pageJson) {
		pageId = pageJson.optString("page_id", "");
		type = pageJson.optString("type", "");
		name = pageJson.optString("name", "");
	}
}
