package edu.psu.cmpsc483w.moviesearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

@SuppressLint("UseSparseArrays")
public class ActorSearchModel {

	private final static String API_KEY = "1b3f7c24642e7cad05978d7a42184b6f";
	private final static String REQUEST_URL = "http://api.themoviedb.org/3/person/";
	
	private Set<MovieListingData> negativeList; 
	// An associative array index by movieId with a pair consisting of the score and the actual 
	// MovieListingData object. That's Java for you.
	private Map<Integer,Pair<Double,MovieListingData>> scoredMovieList;
	
	public ActorSearchModel()
	{
		negativeList = new HashSet<MovieListingData>();
		scoredMovieList = new HashMap<Integer,Pair<Double,MovieListingData>>();
	}
	
	// Empties out the negative list and the rankings of movies
	public void clearAllData()
	{
		negativeList.clear();
		scoredMovieList.clear();
	}
	
	// Returns the current movie list sorted by rank
	public ArrayList<MovieListingData> getRankedMovieList()
	{
		List<Map.Entry<Integer,Pair<Double,MovieListingData>>> list = 
				new LinkedList<Map.Entry<Integer,Pair<Double,MovieListingData>>>(scoredMovieList.entrySet());
		Collections.sort( list, new Comparator<Map.Entry<Integer, Pair<Double,MovieListingData>>>()
		{
			@Override
			public int compare(
					Entry<Integer, Pair<Double, MovieListingData>> lhs,
					Entry<Integer, Pair<Double, MovieListingData>> rhs) {
				if (lhs.getValue().first < rhs.getValue().first)
				{
					return 1;
				}
				else if (lhs.getValue().first > rhs.getValue().first)
				{
					return -1;
				}
				return 0;
			}	
		});
		
		ArrayList<MovieListingData> result = new ArrayList<MovieListingData>();
		
		for (Map.Entry<Integer, Pair<Double,MovieListingData>> entry : list)
		{
			result.add(entry.getValue().second);
		}
		
		return result;
	}
	
	// Searches for an actor's credits and adds all of its entries to the negative list (synchronous)
	public void addNegativeActor(int actorId)
	{
		ArrayList<MovieListingData> newData = executeQuery(actorId);
		
		if (newData != null)
		{
			negativeList.addAll(newData);
		}
	}
	
	// Searches for an actor's credits and updates the ranking
	public void addPositiveActor(int actorId)
	{
		ArrayList<MovieListingData> newData = executeQuery(actorId);
		
		if (newData != null)
		{
			// First, remove all data from newData that is in the negative list
			newData.removeAll(negativeList);
			
			// Then, for each of the remaining items
			for (MovieListingData entry : newData)
			{
				// the increase in score for a given actor is 1.0/newData.size()
				double value = 1.0/newData.size();
				
				// if it already exists, add the current value to the increase in value for the new value
				if (scoredMovieList.containsKey(entry.getId()))
				{
					value += scoredMovieList.get(entry.getId()).first;
				}
				// and regardless of whether it previously existed, commit the entry with the associated value
				scoredMovieList.put(entry.getId(), new Pair<Double,MovieListingData>(value,entry));
			}
		}
	}
	
	// Returns an ArrayList of MovieListingData objects for a given actor id
	private static ArrayList<MovieListingData> executeQuery(int actorId)
	{
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = REQUEST_URL + actorId + "/credits?api_key=" + API_KEY;
		
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
				// The resulting arraylist object
				ArrayList<MovieListingData> movies = new ArrayList<MovieListingData>();
				
				try {
					json = new JSONObject(result);
					
					JSONArray resultsArray = json.getJSONArray("cast");
					
					for (int i=0; i<resultsArray.length(); i++)
					{
						JSONObject entry = resultsArray.getJSONObject(i);
						
						movies.add(new MovieListingData(
								entry.getBoolean("adult"),
								entry.getString("title"),
								entry.getInt("id"),
								entry.getString("release_date"),
								entry.getString("poster_path")
								));
					}
					
					return movies;
					
				} catch (JSONException e) {
					movies = null;
				}
				
				inputStream.close();
				
				return movies;
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
