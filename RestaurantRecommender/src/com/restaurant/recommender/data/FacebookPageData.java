package com.restaurant.recommender.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.restaurant.recommender.utils.Constants;

public class FacebookPageData {
	public String pageId;
	public String type;
	public String name;
	public String location;
	public String workingHours;
	
	public FacebookPageData(JSONObject pageJson) {
		pageId = pageJson.optString("page_id", "");
		type = pageJson.optString("type", "");
	}
	
	public void updateData(JSONObject pageJson) throws JSONException {
		name = pageJson.optString("name", "");
		
		if (pageJson.getJSONObject("location").optString("street", "").equals("")) {
			location = Constants.DEFAULT_CITY;
		} else {
			location = pageJson.getJSONObject("location").optString("street", "") + ", " + Constants.DEFAULT_CITY;
		}
		
		if (pageJson.optJSONObject("hours") != null) {
			workingHours = pageJson.getJSONObject("hours").optString("mon_1_open", "") + " - " + pageJson.getJSONObject("hours").optString("mon_1_close", "");
		} else {
			workingHours = "";
		}
	}
}
