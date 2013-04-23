package edu.psu.cmpsc483w.moviesearch2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class TmdbModel {
	
	// The Api key for the tmdb api registered for this application
	private final static String API_KEY = "1b3f7c24642e7cad05978d7a42184b6f";
	// The base url of all requests made to the tmdb api
	private final static String REQUEST_BASE_URL = "http://api.themoviedb.org/3/";
	
	// Calls the TMDB api with the given request and returns a JSON object with the server's response
	//	@param request a string containing the type of request (following the "/3/" of the url without needing a leading slash)
	//	@param parameters a list of parameters to add to the query
	//	@param parameterValues a list of values for the corresponding strings in parameters
	public static JSONObject executeQuery(String request, String parameters[], String parameterValues[]) {
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = REQUEST_BASE_URL + request + "?api_key=" + API_KEY;
		
		if (parameters != null && parameterValues != null) {
			for (int i=0; i<parameters.length; i++) {
				// Just check if in bounds in case the two arrays are not of equal length
				if (i < parameterValues.length) {
					// Construct the url from the key/value pairs
					url+="&"+parameters[i]+"="+parameterValues[i];
				}
			}
		}
		
		// Make a request object
		HttpGet httpGet = new HttpGet(url);
		
		// Important: Tmdb API requires accept-header as application/json otherwise returns an error
		httpGet.addHeader("Accept", "application/json");
		
		// Execute the request
		HttpResponse response;
		
		// The resulting JSONObject
		JSONObject json;
		
		try {
			response = httpClient.execute(httpGet);
			// Look at the response entity
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				// Start reading the JSON Response
				InputStream inputStream = entity.getContent();
				String result = convertStreamToString(inputStream);
				
				// Parse the string into a JSONObject
				json = new JSONObject(result);
			
				// Close the connection and return the result
				inputStream.close();
				
				return json;
			}	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	// Source: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
	// Converts the InputStream to a String by using a BufferedReader to iterate until it returns null.
	private static String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	
}
