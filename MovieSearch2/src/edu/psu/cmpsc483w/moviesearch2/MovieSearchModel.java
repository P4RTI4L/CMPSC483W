package edu.psu.cmpsc483w.moviesearch2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class MovieSearchModel {
	
	// Synchronously queries the database given a substring of a movie to search for
	//	the synchronous version is provided to make unit testing easier and can be transformed
	//	easily into the asynchronous version
	//
	public static Pair<MovieListingData[],Integer> synchronousMovieSearch(String movieSubstring) {
		return synchronousMovieSearch(movieSubstring, 1);
	}
	
	// Overloaded version of synchronousMovieSearch that allows for specifying the page
	// Omits the page from the query if page is null
	public static Pair<MovieListingData[],Integer> synchronousMovieSearch(String movieSubstring, int page) {
		JSONObject json = TmdbModel.executeQuery("search/movie", new String[]{"query","page"}, 
			new String[]{movieSubstring,Integer.toString(page)});
		
		if (json != null) {
			MovieListingData[] movies;
			Integer numPages;

			try {
			
				JSONArray resultsArray = json.getJSONArray("results");
				numPages = json.getInt("total_pages");
				movies = new MovieListingData[resultsArray.length()];
				
				for (int i=0; i<resultsArray.length(); i++) {
					JSONObject resultsEntry = resultsArray.getJSONObject(i);
					movies[i] = new MovieListingData(
							resultsEntry.getBoolean("adult"),
							resultsEntry.getString("title"),
							resultsEntry.getInt("id"),
							resultsEntry.getString("release_date"),
							resultsEntry.getString("poster_path"));	
				}
				return new Pair<MovieListingData[],Integer>(movies,numPages);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		
		return null;
	}
	
}
