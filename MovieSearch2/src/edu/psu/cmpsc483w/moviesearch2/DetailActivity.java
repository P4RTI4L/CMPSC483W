package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

public class DetailActivity extends Activity {

	private DetailData detailData;
	private ArrayList<String> castNames;
	private ImageModel poster;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		if (savedInstanceState != null) {
			detailData = savedInstanceState.getParcelable("detailData");
			castNames = savedInstanceState.getStringArrayList("castNames");

			poster = new ImageModel(this.getApplicationContext(),
					R.drawable.film_reel);

			setUpDetailData(detailData);
			setUpCastList(castNames);
		} else {
			Intent intent = getIntent();
			new AsyncTaskDetailQuery().execute(
					intent.getIntExtra("movieId", 0),
					DetailModel.DETAIL_PRIMARY);
			new AsyncTaskDetailQuery().execute(
					intent.getIntExtra("movieId", 0), DetailModel.DETAIL_CAST);

			poster = new ImageModel(this.getApplicationContext(),
					R.drawable.film_reel);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("detailData", detailData);
		savedInstanceState.putStringArrayList("castNames", castNames);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	// Sets up the ui
	public void setUpDetailData(DetailData detailData) {
		poster.setImageViewWithUrl(
				(ImageView) findViewById(R.id.detail_movie_poster),
				detailData.getPosterPath(), TmdbModel.POSTER_IMAGE);

		// Set the values of TextViews to their respective value
		((TextView) findViewById(R.id.detail_movie_title)).setText(detailData
				.getTitle());
		((TextView) findViewById(R.id.detail_release_date)).setText(detailData
				.getReleaseDate());
		((TextView) findViewById(R.id.detail_rating)).setText("Rating: "
				+ detailData.getVoteAverage());
		((TextView) findViewById(R.id.detail_tagline)).setText(detailData
				.getTagline());
		((TextView) findViewById(R.id.detail_running_time))
				.setText("Running Time (minutes): " + detailData.getRunTime());

		// Need to convert the genres list to a comma separated string
		String genresList = detailData.getGenres().toString();
		genresList = genresList.substring(1, genresList.length() - 1).replace(
				",", ", ");

		((TextView) findViewById(R.id.detail_genre_list)).setText("Genres: "
				+ genresList);
		((TextView) findViewById(R.id.detail_overview_content))
				.setText(detailData.getOverview());
	}

	public void setUpCastList(ArrayList<String> castNames) {
		TextView castList = (TextView) findViewById(R.id.detail_cast_list);
		String castString = castNames.toString();
		castString = castString.substring(1, castString.length() - 1).replace(
				",", "\n");
		castList.setText(castString);
	}

	// Web queries should always be performed asynchronously to prevent blocking
	// the UI thread, for this purpose
	// an AsyncTask is needed to update the interface as data is ready
	private class AsyncTaskDetailQuery extends
			AsyncTask<Integer, Void, Pair<Integer, Object>> {
		@Override
		protected Pair<Integer, Object> doInBackground(Integer... params) {

			Integer movieId = params[0];
			Integer type = params[1];

			if (type == DetailModel.DETAIL_PRIMARY) {
				Pair<Integer, Object> returnPair = new Pair<Integer, Object>(
						type,
						DetailModel.synchronousDetailPrimaryRetrieve(movieId));

				return returnPair;
			} else if (type == DetailModel.DETAIL_CAST) {
				return new Pair<Integer, Object>(type,
						DetailModel.synchronousDetailCastRetrieve(movieId));
			}

			return null;
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(Pair<Integer, Object> result) {
			if (result != null) {
				if (result.first == DetailModel.DETAIL_PRIMARY) {
					detailData = (DetailData) result.second;
					setUpDetailData(detailData);
				} else {
					castNames = new ArrayList<String>(
							(ArrayList<String>) result.second);
					setUpCastList(castNames);
				}
			}
		}
	}
}
