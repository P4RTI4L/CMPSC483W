package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public abstract class PagedModel implements CachedDataSource, Parcelable {
	// The total number of results according to tmdb
	protected int totalResults;
	// The next page to be read
	protected int nextPage;
	// The data being stored
	protected ArrayList<Object> data;

	public PagedModel() {
		this.data = new ArrayList<Object>();
		// Set the total results to 1 while the data is unknown, as nothing is
		// cached, it'll get the
		this.totalResults = 1;
		// The next page to query at the start should be the first page.
		this.nextPage = 1;
	}

	public PagedModel(Parcel in) {
		this.data = new ArrayList<Object>();
		in.readList(this.data, Object.class.getClassLoader());

		this.totalResults = in.readInt();
		this.nextPage = in.readInt();
	}

	// Fetch new results with whatever method the pagedmodel uses, returns a
	// pair object consisting the of the new
	// results to add to the data and the total number of results
	protected abstract Pair<Object[], Integer> fetchNewResults();

	// Clears all the data and resets to the initial values
	protected void resetData() {
		this.data.clear();
		this.totalResults = 1;
		this.nextPage = 1;
	}

	// Fetches new data until the new position is in the cache, returns true if
	// successful and false otherwise
	protected final boolean fetchUntilCached(int position) {
		// Sanity check, don't continue if it's actually cached since making new
		// requests is expensive
		if (this.isDataCached(position)) {
			return true;
		} else if (position >= this.totalResults) {
			return false;
		}

		Pair<Object[], Integer> newData;

		// Keep fetching results until the data is cached or the db runs out of
		// results (or we hit the request limit)
		do {
			newData = this.fetchNewResults();

			// Query failed for whatever reason, just stop
			if (newData == null) {
				return false;
			}

			// Increment the page if there was data
			if (newData.second > 0) {
				this.nextPage++;
			}

			// Update total results for the first request
			if (newData.second != this.totalResults) {
				this.totalResults = newData.second;
			}
			// If 0 and not the first result (it'll hit the above condition),
			// something bad happened
			// (changed query without resetting or hit the request limit), so
			// just stop since it'll never succeed
			else if (newData.second == 0) {
				return false;
			}

			this.data.addAll(Arrays.asList(newData.first));

		} while (!this.isDataCached(position));

		return true;
	}

	@Override
	public boolean isDataCached(int position) {
		return position < this.data.size();
	}

	@Override
	public int getDataCount() {
		return this.totalResults;
	}

	@Override
	public int getCachedDataCount() {
		return this.data.size();
	}

	@Override
	public Object getData(int position) {
		// If cached, just return the result
		if (this.isDataCached(position) || this.fetchUntilCached(position)) {
			return this.data.get(position);
		}
		// Couldn't get it for some reason
		else {
			return null;
		}
	}

	@Override
	public abstract long getDataId(int position);

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.data);
		dest.writeInt(this.totalResults);
		dest.writeInt(this.nextPage);
	}

}
