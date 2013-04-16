package edu.psu.cmpsc483w.moviesearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.psu.cmpsc483w.moviesearch.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	private ArrayList<MovieListingData> data;
	private String type;
	private final static int MAX_PAGES = 3;

	private MovieResultsArrayAdapter resultsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		if (savedInstanceState != null) {
			data = savedInstanceState.getParcelableArrayList("dataList");
			type = savedInstanceState.getString("type");
		} else {
			data = new ArrayList<MovieListingData>();
			data.add(new MovieListingData(false, "Testing", 1, "Date", "???"));
			Intent intent = getIntent();
			type = intent.getStringExtra("type");

			if (type.equals("movie")) {
				new AsyncTaskMovieQuery().execute(intent
						.getStringExtra("query"));
			}
		}

		ListView resultsList = (ListView) findViewById(R.id.activity_results_listview);
		resultsAdapter = new MovieResultsArrayAdapter(this, data);
		resultsList.setAdapter(resultsAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	// Custom adapter for the movie listings ListView
	private class MovieResultsArrayAdapter extends
			ArrayAdapter<MovieListingData> {
		private final Activity context;
		private final ArrayList<MovieListingData> movies;

		// ViewHolder pattern that reduces calls to findViewById for performance
		// gains when
		// reusing views
		private class ViewHolder {
			public TextView textTitle;
			public TextView textYear;
			public TextView textAuxiliary;
		}

		public MovieResultsArrayAdapter(Activity context,
				ArrayList<MovieListingData> movies) {
			super(context, R.layout.results_row, movies);
			this.context = context;
			this.movies = movies;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View rowView = convertView;

			// No row views available for reuse, so make a new one
			if (rowView == null) {
				rowView = context.getLayoutInflater().inflate(
						R.layout.results_row, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.textTitle = (TextView) rowView
						.findViewById(R.id.results_row_title);
				viewHolder.textYear = (TextView) rowView
						.findViewById(R.id.results_row_release_date);
				viewHolder.textAuxiliary = (TextView) rowView
						.findViewById(R.id.results_row_auxiliary);
				// Set the tag for when it gets reused
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			String title = movies.get(position).getTitle();

			String date = movies.get(position).getReleaseDate();

			holder.textTitle.setText(title);
			holder.textYear.setText(date);
			holder.textAuxiliary.setText("");

			return rowView;
		}
	}

	// Web queries should always be performed asynchronously to prevent blocking
	// the UI thread, for this purpose
	// an AsyncTask is needed to update the interface as data is ready
	private class AsyncTaskMovieQuery extends
			AsyncTask<String, Integer, ArrayList<MovieListingData>> {
		@Override
		protected ArrayList<MovieListingData> doInBackground(String... params) {
			// TODO Auto-generated method stub
			String movieSubstring = params[0];

			ArrayList<MovieListingData> results = new ArrayList<MovieListingData>();

			// Compute the first query and find out the number of pages
			Pair<MovieListingData[], Integer> firstQuery = MovieSearchModel
					.synchronousMovieSearch(movieSubstring);

			Collections.addAll(results, firstQuery.first);

			// Starting from page 2 (if there are any other pages), get the rest
			// of the data until page MAX_PAGE or the rest of the pages
			int maxPages = Math.min(MAX_PAGES, firstQuery.second);

			for (int i = 2; i <= maxPages; i++) {
				Pair<MovieListingData[], Integer> query = MovieSearchModel
						.synchronousMovieSearch(movieSubstring, i);
				Collections.addAll(results, query.first);
			}

			return results;
		}

		@Override
		protected void onPostExecute(ArrayList<MovieListingData> result) {
			// Save the results for later use
			data = result;

			resultsAdapter.notifyDataSetChanged();
		}

	}
}
