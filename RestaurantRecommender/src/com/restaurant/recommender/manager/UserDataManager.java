package com.restaurant.recommender.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.restaurant.recommender.data.FacebookPageData;
import com.restaurant.recommender.data.UserData;
import com.restaurant.recommender.utils.Utils;

public class UserDataManager {
	
	private static UserDataManager instance;
	
	private UserDataManager() {
		
	}
	
	public static UserDataManager $() {
		if (instance == null) {
			instance = new UserDataManager();
		}
		
		return instance;
	}
	
	public UserData userData;
	
	public ArrayList<FacebookPageData> userRestaurantPages = new ArrayList<FacebookPageData>();
	
	public void getUserLikedRestaurants(JSONObject pagesJson) {
		if (pagesJson.has("data")) {
			try {
				String pageType;
				JSONArray pagesJsonArray = pagesJson.getJSONArray("data");
				for (int i = 0; i < pagesJsonArray.length(); i++) {
					pageType = pagesJsonArray.getJSONObject(i).optString("type", "");
					if (Utils.isPageTypeRestaurant(pageType)) {
						FacebookPageData pageData = new FacebookPageData(pagesJsonArray.getJSONObject(i));
						userRestaurantPages.add(pageData);
						Log.d("heghine", "page_id = " + pageData.pageId + " ; type = " + pageData.type);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	
}
