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

	/**
	 * @param topic
	 *            String to set this objects topic to. Should be one of the
	 *            strings listed above.
	 */
	public TopicModel(String topic) {

		super();
		this.topic = topic;
	}

	/**
	 * @param in
	 *            Saved Parcel to read the current topic from.
	 */
	public TopicModel(Parcel in) {

		super(in);

		this.topic = in.readString();
	}

	/**
	 * setNewTopic(String) Sets the topic of this class to the passed topic and
	 * refreshes the UI.
	 * 
	 * @param topic
	 *            The new topic
	 */
	public void setNewTopic(String topic) {
		if (!this.topic.equals(topic)) {
			this.topic = topic;
			super.resetData();
		}
	}

	// Fetch new results with whatever method the pagedmodel uses, returns a
	// pair object consisting the of the new
	// results to add to the data and the total number of results

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.psu.cmpsc483w.moviesearch2.PagedModel#fetchNewResults()
	 */
	@Override
	protected Pair<Object[], Integer> fetchNewResults() {
		return synchronousTopicQuery(this.topic, this.nextPage);
	}

	/**
	 * Gets a list of films associated with the passed topic.
	 * 
	 * @param topic
	 *            The topic to retrieve films for
	 * @param page
	 *            The number of the page to retrieve
	 * @return An array of films and the total number of pages
	 */
	private static Pair<Object[], Integer> synchronousTopicQuery(String topic,
			int page) {
		JSONObject json = TmdbModel.executeQuery("movie/" + topic,
				new String[] { "page" },
				new String[] { Integer.toString(page) });

		if (json != null) {
			try {
				JSONArray jsonResults = json.getJSONArray("results");
				Integer pages = json.getInt("total_pages");

				MovieListingData[] data = new MovieListingData[jsonResults
						.length()];
				for (int i = 0; i < jsonResults.length(); i++) {
					JSONObject entry = jsonResults.getJSONObject(i);

					data[i] = new MovieListingData(false,
							entry.getString("title"), entry.getInt("id"),
							entry.getString("release_date"),
							entry.getString("poster_path"),
							entry.getDouble("vote_average"),
							entry.getInt("vote_count"));
				}

				return new Pair<Object[], Integer>(data, pages);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.psu.cmpsc483w.moviesearch2.PagedModel#getDataId(int)
	 */
	@Override
	public long getDataId(int position) {
		MovieListingData data = (MovieListingData) this.getData(position);

		if (data != null) {
			return data.getId();
		}
		// Default case
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.psu.cmpsc483w.moviesearch2.PagedModel#writeToParcel(android.os.Parcel
	 * , int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		dest.writeString(this.topic);
	}

	public final static Parcelable.Creator<TopicModel> CREATOR = new Parcelable.Creator<TopicModel>() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public TopicModel createFromParcel(Parcel source) {
			return new TopicModel(source);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public TopicModel[] newArray(int size) {
			return new TopicModel[size];
		}

	};

}
