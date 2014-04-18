package com.restaurant.recommender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.restaurant.recommender.R;
import com.restaurant.recommender.manager.UserDataManager;

public class WelcomePageActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_welcome_page);
		
		String name = UserDataManager.$().userData.firstName + " " + UserDataManager.$().userData.lastName;
		((TextView)findViewById(R.id.name)).setText(name);
	}

}
