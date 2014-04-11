package com.restaurant.recommender.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class NetUtils {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if(connec == null) return false;
		
		
		if ((connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null 
				&& connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
				|| (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null 
						&& connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
				//||(connec.getNetworkInfo(ConnectivityManager.TYPE_WIMAX) != null 
					//	&& connec.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).getState() == NetworkInfo.State.CONNECTED)
						) {
			
//			try {
//				
//				InetAddress address = InetAddress.getByName("socialin.com");
//				
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//					return false;
//				}

			//InetAddress.lookupHostByName(InetAddress.java:496)

				return true; 
			}

		return false;
	}
	
	public static boolean isNetworkTypeWifi(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connec == null) return false;
		
		
		if ((connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null 
						&& connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
			) {

				return true; 
			}

		return false;
	}

	public static boolean isConnectedOrConnecting(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (cm != null && nInfo != null && nInfo.isAvailable() && nInfo.isConnected()) 
			return true;
		
		return false;
	}

	public static HttpResponse doGet(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		org.apache.http.params.HttpParams httpparams = httpclient.getParams();
        HttpConnectionParams.setSoTimeout(httpparams, 60000);
        HttpConnectionParams.setConnectionTimeout(httpparams, 10000);
		HttpGet httpget = new HttpGet(url);
		Log.d("NetUtils.doGet() - url:", url);
		// Execute the request
		HttpResponse response;
		response = httpclient.execute(httpget);
		// Examine the response status
		Log.d("NetUtils.doGet() - responseStatus:", response.getStatusLine().toString());
		return response;
	}

	public static HttpResponse doPost(String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		org.apache.http.params.HttpParams httpparams = httpclient.getParams();
        HttpConnectionParams.setSoTimeout(httpparams, 60000);
        HttpConnectionParams.setConnectionTimeout(httpparams, 10000);
		HttpPost httppost = new HttpPost(url);
		// L.d("doPost() - url:",url);
		if (kvPairs != null && kvPairs.isEmpty() == false) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
			String k, v;
			Iterator<String> itKeys = kvPairs.keySet().iterator();
			while (itKeys.hasNext()) {
				k = itKeys.next();
				v = kvPairs.get(k);
				nameValuePairs.add(new BasicNameValuePair(k, v));
				// L.d("doPost() - param:",k," value:",v);
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		HttpResponse response;
		response = httpclient.execute(httppost);
		Log.d("NetUtils.doPost() - responseStatus:", response.getStatusLine().toString());
		return response;
	}

	public static String responseToString(HttpResponse response) {
		StringBuilder sb = new StringBuilder();

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();
		// If the response does not enclose an entity, there is no need
		// to worry about connection release

		try {
			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();

				/*
				 * To convert the InputStream to String we use the
				 * BufferedReader.readLine() method. We iterate until the
				 * BufferedReader return null which means there's no more data
				 * to read. Each line will appended to a StringBuilder and
				 * returned as String.
				 */
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						instream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Closing the input stream will trigger connection release
				instream.close();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String resultString = sb.toString();
		Log.d("NetUtils.responseToString() - response:", resultString);
		return resultString;
	}

//	public static JSONObject getToJson(String url) {
//		JSONObject json = new JSONObject();
//		try {
//			HttpResponse response = doGet(url);
//			json = new JSONObject(responseToString(response));
//		} catch (ClientProtocolException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (IOException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (JSONException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		return json;
//	}
//
//	public static JSONArray getToJsonArray(String url) {
//		JSONArray json = new JSONArray();
//		try {
//			HttpResponse response = doGet(url);
//			String resultString = responseToString(response);
//			if(resultString.startsWith("["))
//				json = new JSONArray(resultString);
//			else {
//				json = new JSONArray();
//				json.put(new JSONObject(resultString));
//			}
//		} catch (ClientProtocolException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (IOException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (JSONException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		return json;
//	}
//
//	public static JSONObject postToJson(String url, Map<String, String> kvPairs) {
//		JSONObject json = new JSONObject();
//		try {
//			HttpResponse response = doPost(url, kvPairs);
//			json = new JSONObject(responseToString(response));
//		} catch (ClientProtocolException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (IOException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (JSONException e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		} catch (Exception e) {
//			Log.e(Log.LOGTAG, e.getMessage(), e);
//			e.printStackTrace();
//		}
//
//		return json;
//	}
//
//	public static void getToJsonAsync(final String url, final RequestObserver observer) {
//		new Thread() {
//			public void run() {
//				JSONObject json = new JSONObject();
//				try {
//					HttpResponse response = doGet(url);
//					json = new JSONObject(responseToString(response));
//					if (observer != null) {
//						observer.onSuccess(json);
//					}
//				} catch (ClientProtocolException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (IOException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (JSONException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (Exception e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				}
//			}
//		}.start();
//	}
//
//	public static void postToJsonAsyc(final String url, final HashMap<String, String> kvPairs, final RequestObserver observer) {
//		new Thread() {
//			public void run() {
//				JSONObject json = new JSONObject();
//				try {
//					HttpResponse response = doPost(url, kvPairs);
//					json = new JSONObject(responseToString(response));
//					if (json.opt("status") != null && json.optString("status").equals("nok")) {
//						observer.onError(json, new Exception("problem on server"));
//					} else {
//						observer.onSuccess(json);
//					}
//				} catch (ClientProtocolException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (IOException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (JSONException e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				} catch (Exception e) {
//					Log.e("NetUtils", e.getMessage(), e);
//					e.printStackTrace();
//					observer.onError(new JSONObject(), e);
//				}
//			}
//		}.start();
//	}

	public static void openUrl(Context context, String url) {
		Intent innerIntent = new Intent(Intent.ACTION_VIEW);
		url = url.startsWith("http") ? url : "http://" + url;
		Log.d("Utils.openUrl() - url:", url);
		innerIntent.setData(Uri.parse(url));
		try {
			context.startActivity(innerIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static InputStream fetch(String address) throws MalformedURLException, IOException {
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.connect();
		InputStream content = connection.getInputStream();

		return content;
	}

	public static String doUpload(InputStream iStream, String uploadUrl, String uploadParam, String remoteFileName) throws IOException {

		URL url;
		HttpURLConnection conn = null;
		InputStream fileInputStream = iStream;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {
			// Create connection
			url = new URL(uploadUrl + "?query=uploadImage&par=" + uploadParam);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			// set post headers
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("Content-Language", "en-US");

			// Uri uri = Uri.parse(remoteFileName);
			// System.out.println("..NetUtils  URI = "+uri);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + remoteFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			// read file and write it into form...
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			fileInputStream.close();
			dos.flush();

			dos.close();

		} catch (MalformedURLException ex) {
			// Log.e(Tag, "error: " + ex.getMessage(), ex);
		} catch (IOException ioe) {
			// Log.e(Tag, "error: " + ioe.getMessage(), ioe);
		}

		try {
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			final StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();

			Log.d("response", response.toString());

			return response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}// DO UPLOAD

}
