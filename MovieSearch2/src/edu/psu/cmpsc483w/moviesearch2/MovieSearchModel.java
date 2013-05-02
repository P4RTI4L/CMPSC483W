package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class MovieSearchModel extends PagedModel {

	// The query being used for the searches
	private String query;

	// Constructor for MovieSearchModel
	public MovieSearchModel(String query) {
		super();

		this.query = query;
	}

	public MovieSearchModel(Parcel source) {
		super(source);

		this.query = source.readString();
	}

	// Clears all the data in the model
	public void clearData() {
		this.query = "";
		super.resetData();
	}

	// Sets the query, clearing all the data if the query is different
	public void setQuery(String query) {
		if (!query.equals(this.query)) {
			this.query = query;

			super.resetData();
		}
	}

	// Fetch new results with whatever method the pagedmodel uses, returns a
	// pair object consisting the of the new
	// results to add to the data and the total number of results
	@Override
	protected Pair<Object[], Integer> fetchNewResults() {
		return synchronousMovieSearch(this.query, this.nextPage);
	}

	// Overloaded version of synchronousMovieSearch that allows for specifying
	// the page for a movie name substring
	// Omits the page from the query if page is null
	private static Pair<Object[], Integer> synchronousMovieSearch(
			String movieSubstring, int page) {
		JSONObject json = TmdbModel.executeQuery("search/movie", new String[] {
				"query", "page" },
				new String[] { movieSubstring, Integer.toString(page) });

		if (json != null) {
			MovieListingData[] movies;
			Integer numResults;

			try {

				JSONArray resultsArray = json.getJSONArray("results");
				numResults = json.getInt("total_results");
				movies = new MovieListingData[resultsArray.length()];

				for (int i = 0; i < resultsArray.length(); i++) {
					JSONObject resultsEntry = resultsArray.getJSONObject(i);
					movies[i] = new MovieListingData(
							resultsEntry.getBoolean("adult"),
							resultsEntry.getString("title"),
							resultsEntry.getInt("id"),
							resultsEntry.getString("release_date"),
							resultsEntry.getString("poster_path"),
							resultsEntry.getDouble("vote_average"),
							resultsEntry.getInt("vote_count"));
				}
				return new Pair<Object[], Integer>(movies, numResults);

			} catch (JSONException e) {
				return null;
			}
		}

		return null;
	}

	@Override
	public long getDataId(int position) {
		MovieListingData data = (MovieListingData) this.getData(position);

		if (data != null) {
			return data.getId();
		}
		// Default case
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		dest.writeString(this.query);
	}
	
	public ArrayList<MovieListingData> getFilteredData(Filter filter) {
		ArrayList<MovieListingData> destination = new ArrayList<MovieListingData>();
		ArrayList<MovieListingData> source = new ArrayList<MovieListingData>();
		
		for (Object object : this.data) {
			source.add((MovieListingData)object);
		}
		
		filter.applyFilters(source, destination);
		
		return destination;
	}

	// All Parcelables MUST have a CREATOR
	public static final Parcelable.Creator<MovieSearchModel> CREATOR = new Parcelable.Creator<MovieSearchModel>() {
		// Create a DetailData object from a Parcel
		@Override
		public MovieSearchModel createFromParcel(Parcel source) {
			return new MovieSearchModel(source);
		}

		@Override
		public MovieSearchModel[] newArray(int size) {
			return new MovieSearchModel[size];
		}

	};
}
