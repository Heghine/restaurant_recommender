package com.restaurant.recommender.data;

import com.facebook.model.GraphUser;

public class UserData {
	public String userId;
	public String fbId;
	public String firstName;
	public String lastName;
	
	public UserData(GraphUser user) {
		userId = "";
		fbId = user.getId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
	}
}
