package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class MovieSearchModel implements CachedDataSource {
	
	// A list of the current movie objects to cache
	private ArrayList<MovieListingData> movies;
	// The query being used for the searches
	private String query;
	// The total number of results according to tmdb
	private int totalResults;
	// The next page to be read
	private int nextPage;
	
	// Constructor for MovieSearchModel
	public MovieSearchModel(String query)
	{
		movies = new ArrayList<MovieListingData>();
		this.query = query;
		// Set the total results to 1 initially so it is forced to find out the actual number
		this.totalResults = 1;
		this.nextPage = 1;
	}
	
	// Synchronously fetches the next page of data to be read and updates the cache, returns the number of results added
	private int fetchNewResults()
	{
		Pair<MovieListingData[],Integer> newData = synchronousMovieSearch(this.query, nextPage);
		
		if (newData == null)
			return 0;
		
		// If the number of results has changed (for the first search primarily), update it
		if (newData.second != totalResults)
		{
			totalResults = newData.second;
		}
		
		// If there was data, make the next query look at the next page, this is mostly necessary in case of a connection problem
		if (newData.first.length > 0)
		{
			nextPage++;
		}
		
		// Add all the new results to the movies arraylist
		movies.addAll(Arrays.asList(newData.first));
		
		return newData.first.length;
	}
	
	// Fetches new data until the new position is in the cache, returns true if successful and false otherwise
	private boolean fetchUntilCached(int position)
	{
		// Sanity check, don't continue if it's actually cached since making new requests is expensive
		if (isDataCached(position))
			return true;
		// Don't bother if the fetch shouldn't be able to succeed
		else if (position >= totalResults)
			return false;
		
		int numNewResults = 0;
		
		// Keep fetching results until the data is cached or the db runs out of results (or we hit the request limit)
		do
		{
			numNewResults = fetchNewResults();
		} while (!isDataCached(position) && numNewResults > 0);
		
		// Either db ran out of results despite the number being reasonable (unlikely) or we hit the request limit
		if (numNewResults == 0)
		{
			return false;
		}
		
		return true;
	}
	
	// Synchronously queries the database given a substring of a movie title to search for.
	//	The synchronous version is provided to make unit testing easier and can be transformed
	//	easily into the asynchronous version
	//
	public static Pair<MovieListingData[],Integer> synchronousMovieSearch(String movieSubstring) {
		return synchronousMovieSearch(movieSubstring, 1);
	}
	
	// Overloaded version of synchronousMovieSearch that allows for specifying the page for a movie name substring
	// Omits the page from the query if page is null
	public static Pair<MovieListingData[],Integer> synchronousMovieSearch(String movieSubstring, int page) {
		JSONObject json = TmdbModel.executeQuery("search/movie", new String[]{"query","page"}, 
			new String[]{movieSubstring,Integer.toString(page)});
		
		if (json != null) {
			MovieListingData[] movies;
			Integer numResults;

			try {
			
				JSONArray resultsArray = json.getJSONArray("results");
				numResults = json.getInt("total_results");
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
				return new Pair<MovieListingData[],Integer>(movies,numResults);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		
		return null;
	}
	
	// Synchronously queries the database for the movies of an actor
	public static MovieListingData[] synchronousCastSearch(int actorIdQuery) {
		JSONObject json = TmdbModel.executeQuery("person/"+actorIdQuery+"/credits", null, null);
		
		try {
			JSONArray castArray = json.getJSONArray("cast");
			MovieListingData results[] = new MovieListingData[castArray.length()];
			
			for (int i=0; i<castArray.length(); i++)
			{
				JSONObject entry = castArray.getJSONObject(i);
				
				results[i] = new MovieListingData(
						entry.getBoolean("adult"),
						entry.getString("title"),
						entry.getInt("id"),
						entry.getString("release_date"),
						entry.getString("poster_path"));
			}
			
			return results;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean isDataCached(int position) {
		return position < movies.size();
	}

	@Override
	public int getDataCount() {
		return totalResults;
	}
	
	@Override
	public int getCachedDataCount() {
		return movies.size();
	}

	@Override
	public Object getData(int position) {
		// If cached, just return the result
		if (isDataCached(position) || fetchUntilCached(position))
		{
			return movies.get(position);
		}
		// Couldn't get it for some reason
		else
		{
			return null;
		}
	}

	@Override
	public long getDataId(int position) {
		MovieListingData data = (MovieListingData)getData(position);
		
		if (data != null)
		{
			return data.getId();
		}
		// Default case
		return 0;
	}
	
}
