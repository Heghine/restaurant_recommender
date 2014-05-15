package com.restaurant.recommender.activity;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.restaurant.recommender.R;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.ItemData;
import com.restaurant.recommender.data.ItemReviewData;
import com.restaurant.recommender.manager.UserDataManager;
import com.restaurant.recommender.utils.Utils;

public class RecommendationsActivity extends Activity {
	
	private RecommendationItemAdapter recommendationAdapter;
	private String moodType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_recommendations);
		moodType = getIntent().getExtras().getString("mood_type");
		
		((ProfilePictureView) findViewById(R.id.profile_picture)).setProfileId(UserDataManager.$().userData.fbId);
		String name = UserDataManager.$().userData.firstName + " " + UserDataManager.$().userData.lastName;
		((TextView)findViewById(R.id.name)).setText(name);
		((TextView)findViewById(R.id.address)).setText(UserDataManager.$().userData.location);
		
		recommendationAdapter = new RecommendationItemAdapter();
		((ListView) findViewById(R.id.recommendations)).setAdapter(recommendationAdapter);
		((ListView) findViewById(R.id.recommendations)).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				int selectedItemId = recommendationAdapter.getItem(position).itemId;
				getItemRevies(selectedItemId);
			}
		});
		
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user == null) {
					UserDataManager.$().clearUserData();
					RecommendationsActivity.this.finish();
				}
			}
		});
	}
	
	public void getItemRevies(final int selectedItemId) {
		if (UserDataManager.$().itemRatings.get(selectedItemId) == null) {
			API.getItemReviews(selectedItemId, new RequestObserver() {
				
				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray reviewsJson = response.getJSONArray("reviews");
					ArrayList<ItemReviewData> itemReviews = new ArrayList<ItemReviewData>();
					for (int i = 0; i < reviewsJson.length(); i++) {
						ItemReviewData itemReviewData = new ItemReviewData(reviewsJson.getJSONObject(i));
						itemReviews.add(itemReviewData);
					}
					UserDataManager.$().itemRatings.append(selectedItemId, itemReviews);
					RecommendationsActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							startRestaurantRatingActivity(selectedItemId);
						}
					});
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
		} else {
			startRestaurantRatingActivity(selectedItemId);
		}
	}
	
	public void startRestaurantRatingActivity(int selectedItemId) {
		Intent restaurantRatingActivity = new Intent(this, RestaurantRatingActivity.class);
		restaurantRatingActivity.putExtra("item_id", selectedItemId);
		startActivity(restaurantRatingActivity);
	}
	
	private class RecommendationItemAdapter extends BaseAdapter {
		
		ArrayList<ItemData> recommdationItems = moodType.equals("") ? UserDataManager.$().recommendationsData : UserDataManager.$().moodRecommendationsData.get(moodType);
		
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
			((TextView) view.findViewById(R.id.rating)).setText(String.valueOf(itemData.rating));
			((TextView) view.findViewById(R.id.rating_count)).setText(Utils.getRatingCountString(itemData.ratingCount));
			
			return view;
		}

	}
	
}
