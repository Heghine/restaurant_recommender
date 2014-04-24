package com.restaurant.recommender.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.restaurant.recommender.R;
import com.restaurant.recommender.RestaurantRecommender;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.ItemData;
import com.restaurant.recommender.manager.UserDataManager;

public class WelcomePageActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_welcome_page);
		
		((ProfilePictureView) findViewById(R.id.profile_picture)).setProfileId(UserDataManager.$().userData.fbId);
		String name = UserDataManager.$().userData.firstName + " " + UserDataManager.$().userData.lastName;
		((TextView)findViewById(R.id.name)).setText(name);
		((TextView)findViewById(R.id.address)).setText(UserDataManager.$().userData.location);
		
		Button sadMoodButton = (Button) findViewById(R.id.mood_melancholy_button);
		sadMoodButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		Button musicMoodButton = (Button) findViewById(R.id.mood_music_button);
		musicMoodButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		Button danceMoodButton = (Button) findViewById(R.id.mood_dance_button);
		danceMoodButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		
		Button coffeeMoodButton = (Button) findViewById(R.id.mood_coffee_button);
		coffeeMoodButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				API.getMoodRecommendations("coffee", new RequestObserver() {
					
					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						Log.d("heghine", response.toString());
						JSONArray recommenderItemsJson = response.getJSONArray("values");
						for (int i = 0; i < recommenderItemsJson.length(); i++) {
							ItemData item = new ItemData(recommenderItemsJson.getJSONObject(i));
							UserDataManager.$().recommendationsData.add(item);
						}
						RestaurantRecommender.$().roActivity.startRecommendationsActivity();
					}
					
					@Override
					public void onError(String response, Exception e) {
						Log.d("heghine", response.toString());
					}
				});
			}
		});
	}
}
