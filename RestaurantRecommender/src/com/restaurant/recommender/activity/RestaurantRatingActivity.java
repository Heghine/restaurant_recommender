package com.restaurant.recommender.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.restaurant.recommender.R;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.ItemData;
import com.restaurant.recommender.data.ItemReviewData;
import com.restaurant.recommender.manager.UserDataManager;
import com.restaurant.recommender.utils.Constants;
import com.restaurant.recommender.utils.Utils;

public class RestaurantRatingActivity extends Activity {
	
	private int itemId;
	private String selectedItemType;
	private RestaurantRatingItemAdapter ratingsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_restaurant_rating);
		
		itemId = getIntent().getIntExtra("item_id", 0);
		ItemData itemData = UserDataManager.$().getItemDataById(itemId);
		
		((ProfilePictureView) findViewById(R.id.picture)).setProfileId(itemData.itemFbId);
		((TextView) findViewById(R.id.name)).setText(itemData.name);
		((TextView) findViewById(R.id.address)).setText(itemData.address);
		((TextView) findViewById(R.id.rating)).setText(String.valueOf(itemData.rating));
		((TextView) findViewById(R.id.rating_count)).setText(Utils.getRatingCountString(itemData.ratingCount));
		
		ratingsAdapter = new RestaurantRatingItemAdapter();
		((ListView) findViewById(R.id.restaurant_reviews)).setAdapter(ratingsAdapter);
		
		((Button) findViewById(R.id.add_review_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startFeedbackDialog();
			}
		});
	}
	
	private class RestaurantRatingItemAdapter extends BaseAdapter {
		
		ArrayList<ItemReviewData> restaurantRatings = UserDataManager.$().itemRatings.get(itemId);
		
		@Override
		public int getCount() {
			return restaurantRatings.size();
		}

		@Override
		public ItemReviewData getItem(int arg0) {
			return restaurantRatings.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int index, View view, ViewGroup group) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) RestaurantRatingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.restaurant_rating_item, group, false);
			}
			
			ItemReviewData itemReviewData = restaurantRatings.get(index);
			
			((ProfilePictureView) view.findViewById(R.id.picture)).setProfileId(itemReviewData.userFbId);
			((TextView) view.findViewById(R.id.name)).setText(itemReviewData.userName);
			((RatingBar) view.findViewById(R.id.rating)).setRating(itemReviewData.rating);
			((TextView) view.findViewById(R.id.review_date)).setText(itemReviewData.date);
			((TextView) view.findViewById(R.id.review)).setText(itemReviewData.reviewText);
			
			return view;
		}

	}

	public void startFeedbackDialog() {
		final Dialog feedbackDialog = new Dialog(RestaurantRatingActivity.this, R.style.Theme_Base_AppCompat_DialogWhenLarge_Base);
		feedbackDialog.setContentView(R.layout.dialog_item_rating);
		feedbackDialog.setTitle(R.string.rating_dialog_title);
		
		RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_FOOD;
		((RadioGroup) feedbackDialog.findViewById(R.id.item_type)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.item_type_coffee:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_COFFEE;
						break;
					case R.id.item_type_dance:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_DANCE;
						break;
					case R.id.item_type_food:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_FOOD;
						break;
					case R.id.item_type_music:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_MUSIC;
						break;
					case R.id.item_type_sad:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_SAD;
						break;
					default:
						RestaurantRatingActivity.this.selectedItemType = Constants.MOOD_RECOMMENDATION_TYPE_COFFEE;
						break;
				}
			}
		});
		
		((Button) feedbackDialog.findViewById(R.id.done)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int rating = (int)((RatingBar) feedbackDialog.findViewById(R.id.rating)).getRating();
				String review = ((EditText) feedbackDialog.findViewById(R.id.review)).getText().toString();
				Log.d("heghine", "rating = " + rating + " --- review = " + review + " --- item_type = " + RestaurantRatingActivity.this.selectedItemType);
				API.setItemReview(itemId, rating, review, RestaurantRatingActivity.this.selectedItemType, new RequestObserver() {
					
					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						Log.d("heghine", "done = " + response.getInt("status"));
					}
					
					@Override
					public void onError(String response, Exception e) {
						
					}
				});
				feedbackDialog.dismiss();
			}
		});		
		feedbackDialog.show();
	}
	
}
