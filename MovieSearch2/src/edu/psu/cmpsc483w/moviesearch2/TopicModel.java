package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class TopicModel implements CachedDataSource, Parcelable {

	public static final String TOPIC_NOW_PLAYING = "now_playing";
	public static final String TOPIC_POPULAR = "popular";
	public static final String TOPIC_TOP_RATED = "top_rated";
	public static final String TOPIC_UPCOMING = "upcoming";
	
	// A list of the current movie objects to cache
	private ArrayList<MovieListingData> movies;
	// The topic being used for the searches
	private String topic;
	// The total number of results according to tmdb
	private int totalResults;
	// The next page to be read
	private int nextPage;
	
	public TopicModel(String topic) {
		movies = new ArrayList<MovieListingData>();
		this.topic = topic;
		this.totalResults = 1;
		this.nextPage = 1;
	}
	
	public TopicModel(Parcel in) {
		this.movies = new ArrayList<MovieListingData>();
		in.readList(this.movies, null);
		
		this.topic = in.readString();
		this.totalResults = in.readInt();
		this.nextPage = in.readInt();
	}
	
	// Synchronously fetches the next page of data to be read and updates the cache, returns the number of results added
	private int fetchNewResults() {
		Pair<MovieListingData[],Integer> newData = synchronousTopicQuery(
				this.topic, nextPage);
		
		if (newData == null)
			return 0;
		
		// If the number of results has changed (for the first search primarily), update it
		if (newData.second != totalResults) {
			totalResults = newData.second;
		}
		
		// If there was data, make the next query look at the next page, this is mostly necessary in case of a connection problem
		if (newData.first.length > 0) {
			nextPage++;
		}
		
		// Add all the new results to the movies arraylist
		movies.addAll(Arrays.asList(newData.first));
		
		return newData.first.length;
	}
	
	// Fetches new data until the new position is in the cache, returns true if 
	// successful and false otherwise
	private boolean fetchUntilCached(int position) {
		// Sanity check, don't continue if it's actually cached since making new 
		// requests is expensive
		if (isDataCached(position))
			return true;
		// Don't bother if the fetch shouldn't be able to succeed
		else if (position >= totalResults)
			return false;
		
		int numNewResults = 0;
		
		// Keep fetching results until the data is cached or the db runs out of results (or we hit the request limit)
		do {
			numNewResults = fetchNewResults();
		} while (!isDataCached(position) && numNewResults > 0);
		
		// Either db ran out of results despite the number being reasonable (unlikely) or we hit the request limit
		if (numNewResults == 0) {
			return false;
		}
		
		return true;
	}

	public static Pair<MovieListingData[],Integer> synchronousTopicQuery(String topic) {
		return synchronousTopicQuery(topic, 1);
	}
	
	public static Pair<MovieListingData[],Integer> synchronousTopicQuery(String topic, int page)
	{
		JSONObject json = TmdbModel.executeQuery("movie/"+topic, new String[]{"page"}, new String[]{Integer.toString(page)});
		
		if (json != null) {
			try {
				JSONArray jsonResults = json.getJSONArray("results");
				Integer pages = json.getInt("total_pages");
				
				MovieListingData[] data = new MovieListingData[jsonResults.length()];
				for (int i=0; i<jsonResults.length(); i++) {
					JSONObject entry = jsonResults.getJSONObject(i);
					
					data[i] = new MovieListingData(false,
							entry.getString("title"), entry.getInt("id"),
							entry.getString("release_date"),
							entry.getString("poster_path"));
				}
				
				return new Pair<MovieListingData[], Integer>(data,pages);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if (isDataCached(position) || fetchUntilCached(position)) {
			return movies.get(position);
		}
		// Couldn't get it for some reason
		else {
			return null;
		}
	}

	@Override
	public long getDataId(int position) {
		MovieListingData data = (MovieListingData)getData(position);
		
		if (data != null) {
			return data.getId();
		}
		// Default case
		return 0;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(this.movies);
		dest.writeString(this.topic);
		dest.writeInt(this.totalResults);
		dest.writeInt(this.nextPage);
	}
	
	public final static Parcelable.Creator<TopicModel> CREATOR = new Parcelable.Creator<TopicModel>() {

		@Override
		public TopicModel createFromParcel(Parcel source) {
			return new TopicModel(source);
		}

		@Override
		public TopicModel[] newArray(int size) {
			return new TopicModel[size];
		}
		
	};
	
}
