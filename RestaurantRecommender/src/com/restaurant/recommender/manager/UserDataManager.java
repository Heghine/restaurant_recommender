package com.restaurant.recommender.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.util.SparseArray;

import com.restaurant.recommender.RestaurantRecommender;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.FacebookPageData;
import com.restaurant.recommender.data.ItemData;
import com.restaurant.recommender.data.ItemReviewData;
import com.restaurant.recommender.data.UserData;
import com.restaurant.recommender.utils.Constants;
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
	public ArrayList<ItemData> recommendationsData = new ArrayList<ItemData>();
	public SparseArray<ArrayList<ItemReviewData>> itemRatings = new SparseArray<ArrayList<ItemReviewData>>();
	
	public HashMap<String, FacebookPageData> userRestaurantPages = new HashMap<String, FacebookPageData>();
	
	public void getUserLikedRestaurants(JSONObject pagesJson) {
		if (pagesJson.has("data")) {
			try {
				String pageType;
				JSONArray pagesJsonArray = pagesJson.getJSONArray("data");
				for (int i = 0; i < pagesJsonArray.length(); i++) {
					pageType = pagesJsonArray.getJSONObject(i).optString("type", "");
					if (Utils.isPageTypeRestaurant(pageType)) {
						FacebookPageData pageData = new FacebookPageData(pagesJsonArray.getJSONObject(i));
						userRestaurantPages.put(pageData.pageId, pageData);
						Log.d("heghine", "page_id = " + pageData.pageId + " ; type = " + pageData.type);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public boolean hasLikedRestaurantPages() {
		return userRestaurantPages.size() != 0;
	}
	
	public void updateUserLikedPageData(JSONObject pagesJson) {
		if (pagesJson.has("data")) {
			try {
				String pageId = "";
				JSONArray pagesJsonArray = pagesJson.getJSONArray("data");
				for (int i = 0; i < pagesJsonArray.length(); i++) {
					pageId = pagesJsonArray.getJSONObject(i).optString("page_id", "");
					userRestaurantPages.get(pageId).updateData(pagesJsonArray.getJSONObject(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setUserLikedPageDataInBackend() {
		if (hasLikedRestaurantPages()) {
			try {
				String preferences = Utils.getUserPreferenceString();
				Log.d("heghine", "user_preferences = " + preferences);
				API.setUserPreferences(preferences, new RequestObserver() {
					
					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						boolean status = response.optInt("status", 0) == 0 ? false : true;
						if (status) {
							getRecommendations();
						}
					}
					
					@Override
					public void onError(String response, Exception e) {
						
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getRecommendations() {
		API.getRecommendations(Constants.RECOMMENDATION_TYPE_PREDICTION, new RequestObserver() {
			
			@Override
			public void onSuccess(JSONObject response) throws JSONException {
				JSONArray recommenderItemsJson = response.getJSONArray("values");
				for (int i = 0; i < recommenderItemsJson.length(); i++) {
					ItemData item = new ItemData(recommenderItemsJson.getJSONObject(i));
					recommendationsData.add(item);
				}
				RestaurantRecommender.$().roActivity.startRecommendationsActivity();
			}
			
			@Override
			public void onError(String response, Exception e) {
				
			}
		});
	}
	
	public ItemData getItemDataById(int itemId) {
		for (int i = 0; i < recommendationsData.size(); i++) {
			if (recommendationsData.get(i).itemId == itemId) {
				return recommendationsData.get(i);
			}
		}
		
		return null;
	}
}
