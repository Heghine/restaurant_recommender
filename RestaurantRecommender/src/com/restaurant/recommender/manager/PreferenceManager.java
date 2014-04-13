package com.restaurant.recommender.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
	
	private static PreferenceManager instance;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	public static final String NAME = "Restaurant.Recommender";
	
	public static final String PREFERENCE_USER_ID = "preference.user.id";
	
	private PreferenceManager() {
		
	}
	
	public static PreferenceManager $() {
		if (instance == null) {
			instance = new PreferenceManager();
		}
		return instance;
	}
	
	public void init(Context context) {
		sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	public String getUserId() {
		return sharedPreferences.getString(PREFERENCE_USER_ID, "0");
	}
	
	public void setUserId(String userId) {
		editor.putString(PREFERENCE_USER_ID, userId);
		editor.commit();
	}
}
