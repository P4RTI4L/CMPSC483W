package edu.psu.cmpsc483w.moviesearch;

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
import android.util.Pair;

public class ActorSubsearchModel {

	private final static String API_KEY = "1b3f7c24642e7cad05978d7a42184b6f";
	private final static String REQUEST_URL = "http://api.themoviedb.org/3/search/person";
	
	// Synchronously queries the database given a substring of a name to search for
	//	the synchronous version is provided to make unit testing easier and can be transformed
	//	easily into the asynchronous version
	//
	public static Pair<ActorData[],Integer> synchronousActorSearch(String nameSubstring)
	{
		return synchronousActorSearch(nameSubstring, null);
	}
	
	// Overloaded version of synchronousActorSearch that allows for specifying the page
	// Omits the page from the query if page is null
	public static Pair<ActorData[],Integer> synchronousActorSearch(String nameSubstring, String page)
	{
		try {
			String url = REQUEST_URL + "?api_key="+ API_KEY + "&query=" + URLEncoder.encode(nameSubstring, "UTF-8");
			if (page != null)
			{
				url += "&page="+page;
			}
			return executeQuery(url);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	// Returns a pair consisting of the actor data and the total number of pages
	private static Pair<ActorData[],Integer> executeQuery(String url)
	{
		HttpClient httpClient = new DefaultHttpClient();
		
		// Make a request object
		HttpGet httpGet = new HttpGet(url);
		
		// Important: Tmdb API requires accept-header as application/json otherwise returns an error
		httpGet.addHeader("Accept", "application/json");
		
		// Execute the request
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpGet);
			// Look at the response entity
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
			{
				// Start reading the JSON Response
				InputStream inputStream = entity.getContent();
				String result = convertStreamToString(inputStream);
				
				// Parse the string into a JSONObject
				JSONObject json;
				// The resulting actor data object array to return in the pair
				ActorData[] actors;
				Integer numPages;
				
				Pair<ActorData[],Integer> resultPair;
				
				try {
					json = new JSONObject(result);
					
					JSONArray resultsArray = json.getJSONArray("results");
					numPages = json.getInt("total_pages"); 
					actors = new ActorData[resultsArray.length()];
					
					// Fill the actors array
					for (int i=0; i<resultsArray.length(); i++)
					{
						JSONObject resultsEntry = resultsArray.getJSONObject(i);
						actors[i] = new ActorData(
								resultsEntry.getBoolean("adult"),
								resultsEntry.getString("name"),
								resultsEntry.getInt("id"),
								resultsEntry.getDouble("popularity"),
								resultsEntry.getString("profile_path"));
					}
					
					resultPair = new Pair<ActorData[],Integer>(actors,numPages);
				} catch (JSONException e) {
					resultPair = null;
				}
				
				inputStream.close();
				
				return resultPair;
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
			while ((line = reader.readLine()) != null)
			{
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
