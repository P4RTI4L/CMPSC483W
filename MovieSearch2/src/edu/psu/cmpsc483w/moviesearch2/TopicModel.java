package edu.psu.cmpsc483w.moviesearch2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class TopicModel extends PagedModel {

	public static final String TOPIC_NOW_PLAYING = "now_playing";
	public static final String TOPIC_POPULAR = "popular";
	public static final String TOPIC_TOP_RATED = "top_rated";
	public static final String TOPIC_UPCOMING = "upcoming";
	
	// The topic being used for the searches
	private String topic;

	public TopicModel(String topic) {
		
		super();
		this.topic = topic;
	}
	
	public TopicModel(Parcel in) {
		
		super(in);

		this.topic = in.readString();
	}
	
	// Fetch new results with whatever method the pagedmodel uses, returns a pair object consisting the of the new
	// results to add to the data and the total number of results
	protected Pair<Object[],Integer> fetchNewResults()
	{
		return synchronousTopicQuery(this.topic, nextPage);
	}
	
	private static Pair<Object[],Integer> synchronousTopicQuery(String topic, int page)
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
							entry.getString("poster_path"),
							entry.getDouble("vote_average"),
							entry.getInt("vote_count"));
				}
				
				return new Pair<Object[], Integer>(data,pages);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
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
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		
		dest.writeString(this.topic);
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
