package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Calendar;

public class Filter {

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
		timeFilter = NO_FILTER;
		ratingFilter = NO_FILTER;
		voteFilter = NO_FILTER;

		customTimeLower = null;
		customTimeUpper = null;

		minRating = 0;
		maxRating = 10;

		voteThreshold = 0;
	}

	public void applyFilters(ArrayList<MovieListingData> source,
			ArrayList<MovieListingData> dest) {
		for (MovieListingData data : source) {
			if (!caughtInFilter(data)) {
				dest.add(data);
			}
		}
	}

	private boolean caughtInFilter(MovieListingData data) {
		if (ratingFilter == FILTER_ENABLED) {
			if (data.getRating() < minRating || data.getRating() > maxRating) {
				return true;
			}
		}

		if (voteFilter == FILTER_ENABLED) {
			if (data.getVoteCount() < voteThreshold) {
				return true;
			}
		}

		if (checkTimeFilter(data)) {
			return true;
		}

		return false;
	}

	// Returns true if data's release date is outside of filter
	private boolean checkTimeFilter(MovieListingData data) {
		if (timeFilter == NO_FILTER) {
			return false;
		}

		Calendar releaseDate = Calendar.getInstance();

		releaseDate.set(Integer.valueOf(data.getReleaseDate().substring(0, 3)),
				Integer.valueOf(data.getReleaseDate().substring(5, 6)),
				Integer.valueOf(data.getReleaseDate().substring(8, 9)));

		Calendar compare = Calendar.getInstance();

		switch (timeFilter) {
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
			if (customTimeLower == null || customTimeUpper == null) {
				return false;
			}

			if (customTimeLower.compareTo(releaseDate) > 0) {
				return true;
			} else if (customTimeUpper.compareTo(releaseDate) < 0) {
				return true;
			}
			break;
		}

		return true;
	}

	// Returns false if filter was not changed (filter out of range)
	public boolean setTimeFilter(int filter) {
		if (filter < NO_FILTER || filter > CUSTOM_TIME) {
			return false;
		}

		timeFilter = filter;

		return true;
	}

	// Returns false if filter was not changed (filter out of range)
	public boolean setTimeFilter(int filter, Calendar lower, Calendar upper) {
		if (!setTimeFilter(filter)) {
			return false;
		}

		customTimeLower = lower;
		customTimeUpper = upper;

		return true;
	}

	public void enableRatingFilter(int min, int max) {
		ratingFilter = FILTER_ENABLED;

		minRating = min;

		maxRating = max;
	}

	public void disableRatingFilter() {
		ratingFilter = NO_FILTER;
	}

	public void enableVoteFilter(int threshold) {
		voteFilter = FILTER_ENABLED;

		voteThreshold = threshold;
	}

	public void disableVoteFilter() {
		voteFilter = NO_FILTER;
	}

}
