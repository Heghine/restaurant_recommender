package com.restaurant.recommender;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.restaurant.recommender.activity.RecommendationsActivity;
import com.restaurant.recommender.activity.WelcomePageActivity;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.UserData;
import com.restaurant.recommender.manager.PreferenceManager;
import com.restaurant.recommender.manager.UserDataManager;
import com.restaurant.recommender.utils.Constants;
import com.restaurant.recommender.utils.Utils;

public class MainActivity extends Activity {
	
	private UiLifecycleHelper uiHelper;
	public RestaurantRecommender restaurantRecommender;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
//	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		restaurantRecommender = new RestaurantRecommender(this); 
		PreferenceManager.$().init(getApplicationContext());
		
		
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user == null) {
					// do log out
				} else {
					initUser(user);
				}
			}
		});
	}

	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    
	    android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	public void requestUserPageLikes() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			String fqlQuery = "SELECT uid, page_id, type FROM page_fan WHERE uid=" + UserDataManager.$().userData.fbId + " LIMIT " + Constants.FACEBOOK_PAGES_LIMIT;
	        Bundle params = new Bundle();
	        params.putString("q", fqlQuery);
	        Request request = new Request(session,
	    		"/fql",                         
	    		params,                         
	    		HttpMethod.GET,                 
	    		new Request.Callback() {         
		        	public void onCompleted(Response response) {
		        		try {
							Log.d("heghine", "" + response.getGraphObject().getInnerJSONObject().getJSONArray("data").length());
						} catch (JSONException e) {
							e.printStackTrace();
						}
		        		initUserFbData(response.getGraphObject().getInnerJSONObject());
		        	}                  
	    		}); 
	        Request.executeBatchAsync(request); 
		} else {
			// do fb connect
		}
	}
	
	public void requestLikedPagesData() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			String pageIds = Utils.getFbPageIdsString();
			String fqlQuery = "SELECT page_id, name, location, hours FROM page WHERE page_id IN(" + pageIds + ")";
			Log.d("heghine", fqlQuery);
	        Bundle params = new Bundle();
	        params.putString("q", fqlQuery);
	        Request request = new Request(session,
	    		"/fql",                         
	    		params,                         
	    		HttpMethod.GET,                 
	    		new Request.Callback() {         
		        	public void onCompleted(Response response) {
		        		UserDataManager.$().updateUserLikedPageData(response.getGraphObject().getInnerJSONObject());
		        		UserDataManager.$().setUserLikedPageDataInBackend();
		        	}                  
	    		}); 
	        Request.executeBatchAsync(request); 
		} else {
			// do fb connect
		}
	}
	
	private void initUser(GraphUser user) {
		try {
			UserDataManager.$().userData = new UserData(user);
			Log.d("heghine", UserDataManager.$().userData.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		PreferenceManager.$().setUserId("4");
		if (PreferenceManager.$().getUserId().equals("0")) {
			Log.d("heghine", "addNewUser --- ");
			API.addNewUser("0", UserDataManager.$().userData.fbId, UserDataManager.$().userData.firstName, UserDataManager.$().userData.lastName, Utils.getGenderCode(UserDataManager.$().userData.gender), UserDataManager.$().userData.location, new RequestObserver() {
				
				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					String userId = response.optString("user_id", "0");
					PreferenceManager.$().setUserId(userId);
					UserDataManager.$().userData.userId = userId;
					API.userId = userId;
					API.userFbId = UserDataManager.$().userData.fbId;
					
					MainActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							requestUserPageLikes();
						}
					});
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
		} else {
			Log.d("heghine", "user_id = " + PreferenceManager.$().getUserId());
			UserDataManager.$().userData.userId = PreferenceManager.$().getUserId();
			API.userId = UserDataManager.$().userData.userId;
			API.userFbId = UserDataManager.$().userData.fbId;
			requestUserPageLikes();
		}
	}
	
	private void initUserFbData(JSONObject data) {
		UserDataManager.$().getUserLikedRestaurants(data);
//		if (UserDataManager.$().hasLikedRestaurantPages()) {
//			requestLikedPagesData();
//		} else {
			// do other method
			startWelcomePageActivity();
//		}
	}
	
	public void startRecommendationsActivity() {
		Intent recommendationsActivity = new Intent(this, RecommendationsActivity.class);
		startActivity(recommendationsActivity);
	}
	
	private void startWelcomePageActivity() {
		Intent welcomePageActivity = new Intent(this, WelcomePageActivity.class);
		startActivity(welcomePageActivity);
	}
}
