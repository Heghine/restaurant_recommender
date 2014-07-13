package com.restaurant.recommender.data;

import org.json.JSONException;

import com.facebook.model.GraphUser;
import com.restaurant.recommender.utils.Constants;

public class UserData {
	public String userId;
	public String fbId;
	public String firstName;
	public String lastName;
	public String location;
	public String gender;
	
	public UserData(GraphUser user) throws JSONException {
		userId = "";
		fbId = user.getId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		gender = user.getInnerJSONObject().optString("gender", "");
		if (user.getInnerJSONObject().has("location")) {
			location = user.getInnerJSONObject().getJSONObject("location").optString("name", "");
			location = location.substring(0, location.indexOf(","));
		} else {
			location = Constants.DEFAULT_CITY;
		}
	}
	
	@Override
	public String toString() {
		String userString = "";
		userString += "userId = " + userId + ";fbId=" + fbId + 
					";name=" + firstName + " " + lastName + 
					";location=" + location + ";gender=" + gender; 
		return userString;
	}
}
