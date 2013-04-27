package edu.psu.cmpsc483w.moviesearch2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TmdbModel {
	
	// The Api key for the tmdb api registered for this application
	private final static String API_KEY = "1b3f7c24642e7cad05978d7a42184b6f";
	// The base url of all requests made to the tmdb api
	private final static String REQUEST_BASE_URL = "http://api.themoviedb.org/3/";
	// The configuration base url for downloading images
	private static String imageBaseUrl;
	// A list of poster sizes available according to the configuration
	private static String posterSizes[];
	// A list of profile_sizes available according to the configuration
	private static String profileSizes[];
	// Constants for specifying image types for the getImageUrl method
	public final static String PROFILE_IMAGE = "profile";
	public final static String POSTER_IMAGE = "poster";
	
	// Returns the image base url if initialized, otherwise queries tmdb for the url and returns that value
	private static String getImageBaseUrl()
	{
		if (imageBaseUrl == null)
		{
			initializeBaseValues();
		}
			
		return imageBaseUrl;
	}
	
	// Returns a complete image url given a type and a relative path (with a leading slash) and a suggested width for the image
	public static String getImageUrl(String type, String path, int width)
	{
		String size;
		
		if (imageBaseUrl == null)
		{
			initializeBaseValues();
		}
		
		// Get a good size for the image
		if (type.equals(PROFILE_IMAGE))
		{
			size = getBestProfileSize(width);
		}
		else if (type.equals(POSTER_IMAGE))
		{
			size = getBestPosterSize(width);
		}
		else
		{
			return null;
		}
		
		return imageBaseUrl + size + path;
		
	}
	
	// Returns the first poster size larger than width, returns the last entry if none of the specified are large enough 
	private static String getBestPosterSize(int width)
	{
		if (posterSizes == null)
		{
			initializeBaseValues();
		}
		
		for (int i=0; i<posterSizes.length; i++)
		{
			// All poster sizes are in the format wx (where x is the width) so check for w first, if not return the last size
			if (posterSizes[i].charAt(0) == 'w')
			{
				if (Integer.valueOf(posterSizes[i].substring(1)) >= width)
				{
					return posterSizes[i];
				}
			}
			else
			{
				break;
			}
		}
		
		return posterSizes[posterSizes.length-1];
	}
	
	// Returns the first profile size larger than width, returns the last entry if none of the specified are large enough 
	private static String getBestProfileSize(int width)
	{
		if (profileSizes == null)
		{
			initializeBaseValues();
		}
		
		for (int i=0; i<profileSizes.length; i++)
		{
			// All profile sizes are in the format wx (where x is the width) so check for w first, if not return the last size
			// there may be a format starting with hy (for height) but just avoid due to layout control
			if (profileSizes[i].charAt(0) == 'w')
			{
				if (Integer.valueOf(profileSizes[i].substring(1)) >= width)
				{
					return profileSizes[i];
				}
			}
			else
			{
				break;
			}
		}
		
		return profileSizes[profileSizes.length-1];
	}
	
	private static void initializeBaseValues()
	{
		JSONObject json = executeQuery("configuration", null, null);
		
		try {
			imageBaseUrl = json.getJSONObject("images").getString("base_url");
			
			JSONArray posterJson = json.getJSONArray("poster_sizes");
			posterSizes = new String[posterJson.length()];
			for (int i=0; i<posterJson.length(); i++) {
				posterSizes[i] = posterJson.getString(i);
			}
			
			JSONArray profileJson = json.getJSONArray("profile_sizes");
			profileSizes = new String[profileJson.length()];
			for (int i=0; i<profileJson.length(); i++)
			{
				profileSizes[i] = profileJson.getString(i);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			imageBaseUrl = null;
			posterSizes = null;
			profileSizes = null;
		}
	}
	
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
					try {
						url+="&"+parameters[i]+"="+URLEncoder.encode(parameterValues[i],"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		Log.i("test", url);
		
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
