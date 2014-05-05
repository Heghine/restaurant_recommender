package com.restaurant.recommender.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.restaurant.recommender.utils.Constants;
import com.restaurant.recommender.utils.NetUtils;
import com.restaurant.recommender.utils.Utils;
import android.util.Log;

public class API {
	public static final String ADD_NEW_USER = "add_new_user";
	public static final String SET_USER_PREFERENCES = "set_user_preferences";
	public static final String GET_RECOMMENDATIONS = "get_recommendations";
	public static final String GET_MOOD_RECOMMENDATIONS = "get_mood_recommendations";
	public static final String GET_ITEM_REVIEWS = "get_item_reviews";
	public static final String SET_ITEM_REVIEW = "set_item_review";
	
	public static final String TAG = "API";
	public static final String secret = "0lymp#@creature5";
	public static final int appVersion = 1;
	
	
	public static void addNewUser(String userId, String userFbId, String firstName, String lastName, int gender, String location, RequestObserver observer) {
		API.userId = userId;
		API.userFbId = userFbId;

		sendRequestAsync(ADD_NEW_USER, "&first_name=" + firstName + "&last_name=" + lastName + "&g=" + gender + "&location=" + location, observer);
	}

	public static void setUserPreferences(String preferences, RequestObserver observer) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("preferences", preferences));
		sendRequestAsync(SET_USER_PREFERENCES, params, RequestObject.POST_METHOD, observer);
	}
	
	public static void getRecommendations(int type, RequestObserver observer) {
		sendRequestAsync(GET_RECOMMENDATIONS, "type=" + type, observer);
	}
	
	public static void getMoodRecommendations(String type, RequestObserver observer) {
		sendRequestAsync(GET_MOOD_RECOMMENDATIONS, "type=" + type, observer);
	}
	
	public static void getItemReviews(int itemId, RequestObserver observer) {
		sendRequestAsync(GET_ITEM_REVIEWS, "item_id=" + itemId, observer);
	} 
	
	public static void setItemReview(int itemId, int rating, String reviewText, String itemType, RequestObserver observer) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("item_id", String.valueOf(itemId)));
		params.add(new BasicNameValuePair("rating", String.valueOf(rating)));
		params.add(new BasicNameValuePair("review_text", reviewText));
		params.add(new BasicNameValuePair("item_type", itemType));
		
		sendRequestAsync(SET_ITEM_REVIEW, params, RequestObject.POST_METHOD, observer);
	}
	
	private static void sendRequestAsync(String command, String params, RequestObserver observer) {
		String paramsString = "&user_id=" + userId + "&fb_id=" + userFbId;
		if (params != null && !params.equals("")) {
			paramsString = paramsString + "&" + params;
		}
		String requestStr = Constants.SERVER_URL + "t=" + command + paramsString;
		sendRequestAsync2(requestStr, command, false, observer);
	}
	
	private static void sendRequestAsync(String command, ArrayList<BasicNameValuePair> params, int requestMethod, RequestObserver observer) {
		params.add(new BasicNameValuePair("user_id", userId));
		params.add(new BasicNameValuePair("user_fb_id", userFbId));
		String requestStr = Constants.SERVER_URL + "t=" + command;

		try {
			sendRequestAsync2(requestStr, command, new UrlEncodedFormEntity(params), requestMethod, observer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendRequestAsync2(String requestStr, String command, HttpEntity params, int requestMethod, RequestObserver observer) {
		RequestObject requestObject = new RequestObject();
		requestObject.requestObserver = observer;
		requestObject.requestString = requestStr;
		requestObject.command = command;
		requestObject.requestMethod = requestMethod;
		requestObject.params = params;
		requestObject.secret = Utils.md5(secret + userId);
		synchronized (requestStack) {
			requestStack.add(requestObject);
			requestStack.notifyAll();
		}
	}
	
	private static RequestObject sendRequestAsync2(String requestStr, String command, boolean silent, RequestObserver observer) {
		RequestObject requestObject = new RequestObject();
		requestObject.requestObserver = observer;
		requestObject.requestString = requestStr;
		requestObject.command = command;
		requestObject.silent = silent;
		requestObject.secret = Utils.md5(secret + userId);
		synchronized (requestStack) {
			requestStack.add(requestObject);
			requestStack.notifyAll();
		}
		return requestObject;
	}

	private static class RequestThread extends Thread {

		private DefaultHttpClient httpClient;

		public RequestThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (requestStack.size() == 0) {
						synchronized (requestStack) {
							requestStack.wait();
						}
					}
					if (requestStack.size() != 0) {
						RequestObject requestObject;
						synchronized (requestStack) {
							requestObject = requestStack.get(0);
						}
						if (httpClient == null) {
							httpClient = new DefaultHttpClient();
						}
						HttpRequestBase request = null;
						if (requestObject.requestMethod == RequestObject.GET_METHOD) {
							request = new HttpGet(requestObject.requestString);
						} else if (requestObject.requestMethod == RequestObject.POST_METHOD) {
							request = new HttpPost(requestObject.requestString);
							((HttpPost) request).setEntity(requestObject.params);
						}
						
						request.setParams(timeoutParams);
						HttpResponse response = null;
						String responseAsString = null;
						int statusCode = -1;
						try {
							Log.d(TAG, "request = " + requestObject.requestString);

							response = httpClient.execute(request);
							statusCode = response.getStatusLine().getStatusCode();
							if (statusCode >= 500) {
								throw new ServerErrorExeption("Server is down, statusCode: " + statusCode);
							}
							responseAsString = NetUtils.responseToString(response);
							Log.d(TAG, "response = " + responseAsString);
							JSONObject jsonObject = null;
							try {
								jsonObject = new JSONObject(responseAsString);
							} catch (JSONException e) {
								JSONArray jsonArray = new JSONArray(responseAsString);
								jsonObject = new JSONObject();
								jsonObject.put("values", jsonArray);
							}
							if (requestObject.requestObserver != null) {
								requestObject.requestObserver.onSuccess(jsonObject);
							}
							

							synchronized (requestStack) {
								requestStack.remove(requestObject);
							}
						} catch (final JSONException e) {
							e.printStackTrace();
							Log.d(TAG, responseAsString);
							if (!requestObject.silent) {
								synchronized (requestStack) {
									requestStack.clear();
								}
//								VikingGame.$().runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										VikingGame.$().showOtherErrorDialogs(Constants.ERROR_CODE_JSON_EXCEPTION, null);
//									}
//								});
//								try {
//									if (responseAsString != null)
//										sendLog(responseAsString.substring(0, responseAsString.length() >= 600 ? 600 : responseAsString.length()), VikingGame.$().activeActivity);
//								} catch (Exception e2) {
//									// no op
//								}
								if (requestObject.requestObserver != null) {
									requestObject.requestObserver.onError(responseAsString, e);
								}

								synchronized (requestStack) {
									requestStack.wait();
								}
							} else {
								synchronized (requestStack) {
									requestStack.remove(requestObject);
								}
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
							if (!requestObject.silent) {
//								VikingGame.$().activeActivity.runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										VikingGame.$().showNetworkLostDialog(true);
//									}
//								});
								synchronized (requestStack) {
									requestStack.wait();
								}
							} else {
								synchronized (requestStack) {
									requestStack.remove(requestObject);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
							if (!requestObject.silent) {
//								VikingGame.$().activeActivity.runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										VikingGame.$().showNetworkLostDialog(true);
//									}
//								});

								synchronized (requestStack) {
									requestStack.wait();
								}
							} else {
								synchronized (requestStack) {
									requestStack.remove(requestObject);
								}
							}
						} catch (ServerErrorExeption e) {
							e.printStackTrace();
							if (!requestObject.silent) {
//								final int statusCodeFinal = statusCode;
//								VikingGame.$().activeActivity.runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										VikingGame.$().showServerErrorDialog(statusCodeFinal);
//									}
//								});
								synchronized (requestStack) {
									requestStack.wait();
								}
							} else {
								synchronized (requestStack) {
									requestStack.remove(requestObject);
								}
							}
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				Log.d(TAG, "api is interapted " + e);
			}
		}
	}

	@SuppressWarnings("serial")
	private static class ServerErrorExeption extends Exception {
		public ServerErrorExeption(String msg) {
			super(msg);
		}
	}
	
	public static class RequestObject {
		RequestObserver requestObserver;
		String requestString;
		String command;
		String secret;
		final static int POST_METHOD = 1;
		final static int GET_METHOD = 2;
		int requestMethod = GET_METHOD;
		HttpEntity params = null;
		boolean silent = false;
		
		public void setRequestObserver(RequestObserver requestObj){
			 requestObserver = requestObj;
		}
	}

	private static RequestThread requestThread = new RequestThread();
	private static LinkedList<RequestObject> requestStack = new LinkedList<RequestObject>();
	public static HashMap<String, RequestObject> canceledRequests = new HashMap<String,RequestObject>();

	public static String userId = "";
	public static String userFbId = "";

	public static interface RequestObserver {
		public void onSuccess(JSONObject response) throws JSONException;

		public void onError(String response, Exception e);
	}

	public static abstract class SimpleRequestObserver implements RequestObserver {
		public void onError(String response, Exception e) {

		}
	}

	public static void resend(final RequestObserver observer) {
		synchronized (requestStack) {
			requestStack.notifyAll();
		}
	}
	
	public static void stopThread() {
		requestThread.interrupt();
	}

	public static final int CONNECTION_TIMEOUT = 120000;
	public static final int WAIT_RESPONSE_TIMEOUT = 120000;
	private static final HttpParams timeoutParams;
	static {
		requestThread.start();
		requestThread.setPriority(Thread.NORM_PRIORITY - 1);

		timeoutParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(timeoutParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(timeoutParams, WAIT_RESPONSE_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(timeoutParams, true);
	}
}