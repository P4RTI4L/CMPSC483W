package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {

	public static final int NO_FILTER = 0;
	public static final int FILTER_ENABLED = 1;

	public static final int ONE_MONTH = 1;
	public static final int THREE_MONTHS = 2;
	public static final int ONE_YEAR = 3;
	public static final int CUSTOM_TIME = 4;

	private int timeFilter;
	private Calendar customTimeLower;
	private Calendar customTimeUpper;

	private int ratingFilter;
	private int minRating;
	private int maxRating;

	private int voteFilter;
	private int voteThreshold;

	public Filter() {
		this.timeFilter = NO_FILTER;
		this.ratingFilter = NO_FILTER;
		this.voteFilter = NO_FILTER;

		this.customTimeLower = null;
		this.customTimeUpper = null;

		this.minRating = 0;
		this.maxRating = 10;

		this.voteThreshold = 0;
	}

	public Filter(Parcel in) {
		this.timeFilter = in.readInt();
		this.customTimeLower = (Calendar) in.readSerializable();
		this.customTimeUpper = (Calendar) in.readSerializable();
		this.ratingFilter = in.readInt();
		this.minRating = in.readInt();
		this.maxRating = in.readInt();
		this.voteFilter = in.readInt();
		this.voteThreshold = in.readInt();
	}

	public void applyFilters(ArrayList<MovieListingData> source,
			ArrayList<MovieListingData> dest) {
		for (MovieListingData data : source) {
			if (!this.caughtInFilter(data)) {
				dest.add(data);
			}
		}
	}

	private boolean caughtInFilter(MovieListingData data) {
		if (this.ratingFilter == FILTER_ENABLED) {
			if ((data.getRating() < this.minRating)
					|| (data.getRating() > this.maxRating)) {
				return true;
			}
		}

		if (this.voteFilter == FILTER_ENABLED) {
			if (data.getVoteCount() < this.voteThreshold) {
				return true;
			}
		}

		if (this.checkTimeFilter(data)) {
			return true;
		}

		return false;
	}

	// Returns true if data's release date is outside of filter
	private boolean checkTimeFilter(MovieListingData data) {
		if (this.timeFilter == NO_FILTER) {
			return false;
		}

		Calendar releaseDate = Calendar.getInstance();

		// TMDb always sends dates as YYYY-MM-DD
		releaseDate.set(Integer.valueOf(data.getReleaseDate().substring(0, 3)),
				Integer.valueOf(data.getReleaseDate().substring(5, 6)),
				Integer.valueOf(data.getReleaseDate().substring(8, 9)));

		Calendar compare = Calendar.getInstance();

		switch (this.timeFilter) {
		case ONE_MONTH:
			compare.add(Calendar.MONTH, -1);

			if (releaseDate.compareTo(compare) < 0) {
				return true;
			}
			break;

		case THREE_MONTHS:
			compare.add(Calendar.MONTH, -3);

			if (releaseDate.compareTo(compare) < 0) {
				return true;
			}
			break;

		case ONE_YEAR:
			compare.add(Calendar.YEAR, -1);

			if (releaseDate.compareTo(compare) < 0) {
				return true;
			}
			break;

		case CUSTOM_TIME:
			// Sanity check
			if ((this.customTimeLower == null)
					|| (this.customTimeUpper == null)) {
				return false;
			}

			if (this.customTimeLower.compareTo(releaseDate) > 0) {
				return true;
			} else if (this.customTimeUpper.compareTo(releaseDate) < 0) {
				return true;
			}
			break;

		default:
			break;
		}

		return false;
	}

	// Accessors

	public int getTimeFilter() {
		return this.timeFilter;
	}

	public Calendar getCustomTimeLower() {
		return this.customTimeLower;
	}

	public Calendar getCustomTimeUpper() {
		return this.customTimeUpper;
	}

	public int getRatingFilter() {
		return this.ratingFilter;
	}

	public int getMinRating() {
		return this.minRating;
	}

	public int getMaxRating() {
		return this.maxRating;
	}

	public int getVoteFilter() {
		return this.voteFilter;
	}

	public int getVoteThreshold() {
		return this.voteThreshold;
	}

	// Returns false if filter was not changed (filter out of range)
	public boolean setTimeFilter(int filter) {
		if ((filter < NO_FILTER) || (filter > CUSTOM_TIME)) {
			return false;
		}

		this.timeFilter = filter;

		return true;
	}

	// Returns false if filter was not changed (filter out of range)
	public boolean setTimeFilter(int filter, Calendar lower, Calendar upper) {
		if (!this.setTimeFilter(filter)) {
			return false;
		}

		this.customTimeLower = lower;
		this.customTimeUpper = upper;

		return true;
	}

	public void setUpperTimeLimit(Calendar upper) {
		this.customTimeUpper = upper;
	}

	public void setLowerTimeLimit(Calendar lower) {
		this.customTimeLower = lower;
	}

	public void enableRatingFilter(int min, int max) {
		this.ratingFilter = FILTER_ENABLED;

		this.minRating = min;

		this.maxRating = max;
	}

	public void setMinRating(int min) {
		this.minRating = min;
	}

	public void setMaxRating(int max) {
		this.maxRating = max;
	}

	public void disableRatingFilter() {
		this.ratingFilter = NO_FILTER;
	}

	public void enableVoteFilter(int threshold) {
		this.voteFilter = FILTER_ENABLED;

		this.voteThreshold = threshold;
	}

	public void disableVoteFilter() {
		this.voteFilter = NO_FILTER;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.timeFilter);
		dest.writeSerializable(this.customTimeLower);
		dest.writeSerializable(this.customTimeUpper);
		dest.writeInt(this.ratingFilter);
		dest.writeInt(this.minRating);
		dest.writeInt(this.maxRating);
		dest.writeInt(this.voteFilter);
		dest.writeInt(this.voteThreshold);
	}

	// All Parcelables MUST have a CREATOR
	public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
		// Create a DetailData object from a Parcel
		@Override
		public Filter createFromParcel(Parcel source) {
			return new Filter(source);
		}

		@Override
		public Filter[] newArray(int size) {
			return new Filter[size];
		}

	};

}
