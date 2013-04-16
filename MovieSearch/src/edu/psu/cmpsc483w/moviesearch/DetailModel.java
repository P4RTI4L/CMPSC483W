package edu.psu.cmpsc483w.moviesearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailModel {
	
	private final static String API_KEY = "1b3f7c24642e7cad05978d7a42184b6f";
	private final static String REQUEST_PRIMARY_URL = "http://api.themoviedb.org/3/movie/";
	
	public final static int DETAIL_PRIMARY = 0;
	public final static int DETAIL_CAST = 1;
	
	// Synchronously queries the database given an id of a movie for the basic information of a movie
	public static DetailData synchronousDetailPrimaryRetrieve(int movieId)
	{
		JSONObject result = synchronousDetailQuery(movieId, DETAIL_PRIMARY);
		
		if (result != null)
		{
			try {
			JSONArray genresList = result.getJSONArray("genres");
			ArrayList<String> genres = new ArrayList<String>();
			for (int i=0; i<genresList.length(); i++)
			{
				genres.add(genresList.getJSONObject(i).getString("name"));
			}

			
			return new DetailData(
				result.getBoolean("adult"),
				genres,
				result.getInt("id"),
				result.getString("overview"),
				result.getDouble("popularity"),
				result.getString("poster_path"),
				result.getString("release_date"),
				result.getInt("runtime"),
				result.getString("title"),
				result.getString("tagline"),
				result.getDouble("vote_average")		
					);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	// Synchronously queries the database given an id of a movie for a list of the names of the actors
	public static ArrayList<String> synchronousDetailCastRetrieve(int movieId)
	{
		JSONObject query = synchronousDetailQuery(movieId, DETAIL_CAST);
		
		if (query != null)
		{
			ArrayList<String> result = new ArrayList<String>();
			
			try {
				JSONArray cast = query.getJSONArray("cast");
				
				for (int i=0; i<cast.length(); i++)
				{
					result.add(cast.getJSONObject(i).getString("name"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return result;
		}
		
		return null;
	}
	
	// Synchronously performs a query given a movieId depending on the type of data requested and returns
	// the JSONObject containing the response
	private static JSONObject synchronousDetailQuery(int movieId, int type)
	{
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = REQUEST_PRIMARY_URL + movieId + (type == DETAIL_PRIMARY ? "" : "/casts") + "?api_key=" + API_KEY;
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
				JSONObject json = new JSONObject(result);
				
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

