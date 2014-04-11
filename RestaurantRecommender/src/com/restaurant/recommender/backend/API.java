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
import android.content.Context;
import android.util.Log;

public class API {
	public static final String GET_USER_INFO = "get_user_info";
	public static final String CONNECT_FB = "connect_fb";
	public static final String SYNC_CAGE = "sync_cage";
	public static final String HELLO_MESSAGE = "hello.php";
	
	public static final String LOG = "birdland_api";
	
	
	public static final String TAG = "API";
	
	public static final String secret = "0lymp#@creature5";
	public static final int appVersion = 1;//SocialinApplication.APP_VERSION;
	
	public static HashMap<String, RequestObject> canceledRequests = new HashMap<String,RequestObject>();

	
	public static void getUserInfoAsyncByUserId(String userId, String socialinId, String updateSocialinId, int coins, int feather, long exp, int food, int[] size, String referrerId, RequestObserver observer,
			String lang, boolean updateUserId) {
		API.userId = socialinId;

		Log.e("language", "api: preference lang: " + lang);
		String eDeviceId = "";//Utils.getAndroidId(VikingGame.$().roActivity, true);

		String requestStr = new StringBuilder().append(Constants.SERVER_URL).append("t=").append(GET_USER_INFO).append("&money=").append(coins).append("&exp=").append(exp).append("&feather=").append(feather).append("&food=")
				.append(food).append("&width=").append(size[1]).append("&height=").append(size[0]).append("&lang=").append(lang).append("&armando=").append(eDeviceId).toString();
		if (referrerId != null && !referrerId.equals("")) {
			requestStr += "&referrer=" + referrerId;
		}
		if (userId != null) {
			requestStr += "&user_id=" + userId;
			API.userId = userId;
		} else if (socialinId != null && !socialinId.equals("null")) {
			requestStr += "&socialin_id=" + socialinId;
		}

		sendRequestAsync2(requestStr, GET_USER_INFO, false, observer);
	}

	public static void connectToFb(String fbId, String userFbFriendsList, RequestObserver observer) {
		sendRequestAsync(CONNECT_FB, "fbId=" + fbId +  "&friend_list=" + userFbFriendsList, observer);
	}
	

	public static void syncCage(String userCurCageId, int sendingMoney, int sendingExp, int sendingFeather, int sendingFood, JSONArray items, String syncBirds, String syncItems, JSONArray expiredGifts, RequestObserver observer) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (sendingMoney != 0)
			params.add(new BasicNameValuePair("sending_money", String.valueOf(sendingMoney)));
		if (sendingExp != 0)
			params.add(new BasicNameValuePair("sending_exp", String.valueOf(sendingExp)));
		if (sendingFeather != 0)
			params.add(new BasicNameValuePair("sending_feather", String.valueOf(sendingFeather)));
		if (sendingFood != 0)
			params.add(new BasicNameValuePair("sending_food", String.valueOf(sendingFood)));
		if (items != null && items.length() > 0)
			params.add(new BasicNameValuePair("items", items.toString()));
		if (!syncBirds.equals(""))
			params.add(new BasicNameValuePair("parrots", syncBirds));
		if (!syncItems.equals(""))
			params.add(new BasicNameValuePair("sync_items", syncItems));
		if (expiredGifts != null && expiredGifts.length() > 0)
			params.add(new BasicNameValuePair("expired_gifts", expiredGifts.toString()));
		
		params.add(new BasicNameValuePair("user_current_cage_id", userCurCageId));

		Log.i(TAG, "syning cage : " + params.toString());

		sendRequestAsync(SYNC_CAGE, params, observer, RequestObject.POST_METHOD);
	}
	
	public static void sendHelloMessage(RequestObserver observer) {
		sendRequestAsync(HELLO_MESSAGE, "", observer);
	}
	
	public static void logMessage(String logMessage, Number colin, RequestObserver observer) {
		sendRequestAsync(LOG, "error=" + logMessage + "&colin=" + colin, observer);
	}

	public static void resend(final RequestObserver observer) {
		synchronized (requestStack) {
			requestStack.notifyAll();
		}
	}

	private static void sendRequestAsync(String command, String params, RequestObserver observer) {
		String paramsString = "";
//		String paramsString = "&user_id=" + userId;
//		if (params != null && !params.equals("")) {
//			paramsString = paramsString + "&" + params;
//		}
//		String requestStr = Constants.SERVER_URL + "t=" + command + paramsString;
		String requestStr = Constants.SERVER_URL + command + paramsString;
		sendRequestAsync2(requestStr, command, false, observer);
	}

	private static RequestObject sendRequestAsync2(String requestStr, String command, boolean silent, RequestObserver observer) {
		RequestObject requestObject = new RequestObject();
		requestObject.requestObserver = observer;
		requestObject.requestString = requestStr;
		requestObject.command = command;
		requestObject.silent = silent;
		requestObject.secret = Utils.md5(secret + userId);
//		requestObject.requestString = requestObject.requestString + "&sig=" + requestObject.secret + "&v=" + appVersion;
		synchronized (requestStack) {
			requestStack.add(requestObject);
			requestStack.notifyAll();
		}
		return requestObject;
	}

	private static void sendRequestAsync(String command, ArrayList<BasicNameValuePair> params, RequestObserver observer, int requestMethod) {
		params.add(new BasicNameValuePair("user_id", userId));
		String requestStr = Constants.SERVER_URL + "t=" + command;

		try {
			sendRequestAsync2(requestStr, command, new UrlEncodedFormEntity(params), observer, requestMethod);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static void sendRequestAsync2(String requestStr, String command, HttpEntity params, RequestObserver observer, int requestMethod) {
		RequestObject requestObject = new RequestObject();
		requestObject.requestObserver = observer;
		requestObject.requestString = requestStr;
		requestObject.command = command;
		requestObject.requestMethod = requestMethod;
		requestObject.params = params;
		requestObject.secret = Utils.md5(secret + userId);
//		requestObject.requestString = requestObject.requestString + "&sig=" + requestObject.secret + "&v=" + appVersion;
		synchronized (requestStack) {
			requestStack.add(requestObject);
			requestStack.notifyAll();
		}
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
						Log.d(TAG, "requestStack is not empty");
						RequestObject requestObject;
						synchronized (requestStack) {
							requestObject = requestStack.get(0);
						}
						if (httpClient == null)
							httpClient = new DefaultHttpClient();
						HttpRequestBase request = null;
						if (requestObject.requestMethod == RequestObject.GET_METHOD) {
							request = new HttpGet(requestObject.requestString);
						} else if (requestObject.requestMethod == RequestObject.POST_METHOD) {
							request = new HttpPost(requestObject.requestString);
							((HttpPost) request).setEntity(requestObject.params);
						}
						
						request.setParams(timeoutParams);
						// Execute the request
						HttpResponse response = null;
						String responseAsString = null;
						int statusCode = -1;
						try {
							Log.d(TAG, "request = " + requestObject.requestString);
							Log.d(TAG, "request = " + request.toString());
//							debugLog(requestObject, null, requestObject.command);

							response = httpClient.execute(request);
							statusCode = response.getStatusLine().getStatusCode();
							Log.d(TAG, "statusCode = " + statusCode);
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
//							debugLog(requestObject, jsonObject, requestObject.command);
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
//							debugLog(requestObject, null, requestObject.command);
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
						}
						// catch (SocketException e) {
						// e.printStackTrace();
						// }
						catch (IOException e) {
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

	public static String userId = "";

	public static interface RequestObserver {
		public void onSuccess(JSONObject response) throws JSONException;

		public void onError(String response, Exception e);
	}

	public static abstract class SimpleRequestObserver implements RequestObserver {
		public void onError(String response, Exception e) {

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

	public static void sendLog(final String logStr, final Context context) {
//		if ((UserDataManager.$() != null && UserDataManager.$().userData != null && UserDataManager.$().userData.configData.sendHandledErrors) || UserDataManager.$() == null) {
//			new Thread() {
//				@Override
//				public void run() {
//					try {
//
//						long playTime = System.currentTimeMillis() - PreferenceManager.$().getLastInMillis();
//
//						DefaultHttpClient httpClient = new DefaultHttpClient();
//						PackageManager pm = context.getPackageManager();
//						PackageInfo pi;
//
//						pi = pm.getPackageInfo(context.getPackageName(), 0);
//						//HttpPost httpPost = new HttpPost("http://socialin.com/services/exception.php");
//						HttpPost httpPost = new HttpPost("http://fvpgame.com/exception_handler/exception.php");
//						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.SECURITY_TOKEN, "SocialInGames2011"));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.APPLICATION_VERSION, pi.versionName + "_handledexceptions"));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.APPLICATION_PACKAGE, pi.packageName));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.PHONE_MODEL, android.os.Build.MODEL));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.ANDROID_VERSION, android.os.Build.VERSION.RELEASE));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.APPLICATION_STACKTRACE, logStr));
//						nvps.add(new BasicNameValuePair(com.firegnom.rat.util.Constants.ADDITIONAL_DATA, "socialin_id:" + API.userId + ", play_time_minutes:" + (float) playTime / (1000f * 60f)));
//
//						nvps.add(new BasicNameValuePair("handled", "1"));
//						httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
//						httpClient.execute(httpPost);
//
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					} catch (ClientProtocolException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (NameNotFoundException e1) {
//						e1.printStackTrace();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}.start();
//		}
	}
}