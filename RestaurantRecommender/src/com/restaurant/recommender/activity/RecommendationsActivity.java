package com.restaurant.recommender.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.restaurant.recommender.R;
import com.restaurant.recommender.data.ItemData;
import com.restaurant.recommender.manager.UserDataManager;

public class RecommendationsActivity extends Activity {
	
	RecommendationItemAdapter recommendationAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_recommendations);
		
		((ProfilePictureView) findViewById(R.id.profile_picture)).setProfileId(UserDataManager.$().userData.fbId);
		String name = UserDataManager.$().userData.firstName + " " + UserDataManager.$().userData.lastName;
		((TextView)findViewById(R.id.name)).setText(name);
		
		recommendationAdapter = new RecommendationItemAdapter();
		((ListView) findViewById(R.id.recommendations)).setAdapter(recommendationAdapter);
	}
	
	private class RecommendationItemAdapter extends BaseAdapter {
		
		ArrayList<ItemData> recommdationItems = UserDataManager.$().recommendationsData;
		
		@Override
		public int getCount() {
			return recommdationItems.size();
		}

		@Override
		public ItemData getItem(int arg0) {
			return recommdationItems.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int index, View view, ViewGroup group) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) RecommendationsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.recommendation_item, group, false);
			}
			
			ItemData itemData = recommdationItems.get(index);
			
			((ProfilePictureView) view.findViewById(R.id.picture)).setProfileId(itemData.itemFbId);
			((TextView) view.findViewById(R.id.name)).setText(itemData.name);
			((TextView) view.findViewById(R.id.address)).setText(itemData.address);
			((TextView) view.findViewById(R.id.rating)).setText("" + itemData.rating);
			((TextView) view.findViewById(R.id.rating_count)).setText("" + itemData.ratingCount);
			
			return view;
		}

	}
	
}
