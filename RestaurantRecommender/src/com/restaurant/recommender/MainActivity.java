package com.restaurant.recommender;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.restaurant.recommender.backend.API;
import com.restaurant.recommender.backend.API.RequestObserver;
import com.restaurant.recommender.data.UserData;
import com.restaurant.recommender.manager.PreferenceManager;
import com.restaurant.recommender.manager.UserDataManager;
import com.restaurant.recommender.utils.Utils;

public class MainActivity extends FragmentActivity {
	
	private UiLifecycleHelper uiHelper;
	
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
		
		setContentView(R.layout.fragment_main);
		
		PreferenceManager.$().init(getApplicationContext());
		
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				initUser(user);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			String fqlQuery = "SELECT uid, page_id, type FROM page_fan WHERE uid=" + UserDataManager.$().userData.fbId;
	        Bundle params = new Bundle();
	        params.putString("q", fqlQuery);
	        Request request = new Request(session,
	    		"/fql",                         
	    		params,                         
	    		HttpMethod.GET,                 
	    		new Request.Callback() {         
		        	public void onCompleted(Response response) {
		        		UserDataManager.$().getUserLikedRestaurants(response.getGraphObject().getInnerJSONObject());
		        	}                  
	    		}); 
	        Request.executeBatchAsync(request); 
		} else {
			// do fb connect
		}
	}
	
	private void initUser(GraphUser user) {
		if (user != null) {
			try {
				UserDataManager.$().userData = new UserData(user);
				Log.d("heghine", UserDataManager.$().userData.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (PreferenceManager.$().getUserId().equals("0")) {
				Log.d("heghine", "addNewUser --- ");
				API.addNewUser("0", UserDataManager.$().userData.fbId, UserDataManager.$().userData.firstName, UserDataManager.$().userData.lastName, Utils.getGenderCode(UserDataManager.$().userData.gender), UserDataManager.$().userData.location, new RequestObserver() {
					
					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						String userId = response.optString("user_id", "0");
						PreferenceManager.$().setUserId(userId);
						API.userId = userId;
						UserDataManager.$().userData.userId = userId;
						
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
				requestUserPageLikes();
			}
		}
	}
}
