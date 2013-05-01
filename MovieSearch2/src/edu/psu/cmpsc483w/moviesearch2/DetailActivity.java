package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private DetailData detailData;
	private ArrayList<String> castNames;
	private ImageModel poster;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_detail);

		if (savedInstanceState != null) {
			this.detailData = savedInstanceState.getParcelable("detailData");
			this.castNames = savedInstanceState.getStringArrayList("castNames");

			this.poster = new ImageModel(this.getApplicationContext(),
					R.drawable.film_reel);

			this.setUpDetailData(this.detailData);
			this.setUpCastList(this.castNames);
		} else {
			Intent intent = this.getIntent();
			new AsyncTaskDetailQuery().execute(
					intent.getIntExtra("movieId", 0),
					DetailModel.DETAIL_PRIMARY);
			new AsyncTaskDetailQuery().execute(
					intent.getIntExtra("movieId", 0), DetailModel.DETAIL_CAST);

			this.poster = new ImageModel(this.getApplicationContext(),
					R.drawable.film_reel);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("detailData", this.detailData);
		savedInstanceState.putStringArrayList("castNames", this.castNames);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	// Sets up the ui
	public void setUpDetailData(DetailData detailData) {
		this.poster.setImageViewWithUrl(
				(ImageView) this.findViewById(R.id.detail_movie_poster),
				detailData.getPosterPath(), TmdbModel.POSTER_IMAGE);

		// Set the values of TextViews to their respective value
		((TextView) this.findViewById(R.id.detail_movie_title))
				.setText(detailData.getTitle());
		((TextView) this.findViewById(R.id.detail_release_date))
				.setText(detailData.getReleaseDate());
		((TextView) this.findViewById(R.id.detail_rating)).setText("Rating: "
				+ detailData.getVoteAverage());
		((TextView) this.findViewById(R.id.detail_tagline)).setText(detailData
				.getTagline());
		((TextView) this.findViewById(R.id.detail_running_time))
				.setText("Running Time (minutes): " + detailData.getRunTime());

		// Need to convert the genres list to a comma separated string
		String genresList = detailData.getGenres().toString();
		genresList = genresList.substring(1, genresList.length() - 1).replace(
				",", ", ");

		((TextView) this.findViewById(R.id.detail_genre_list))
				.setText("Genres: " + genresList);
		((TextView) this.findViewById(R.id.detail_overview_content))
				.setText(detailData.getOverview());
	}

	public void setUpCastList(ArrayList<String> castNames) {
		TextView castList = (TextView) this.findViewById(R.id.detail_cast_list);
		String castString = castNames.toString();
		castString = castString.substring(0, castString.length() ).replace(
				",", "\n").replace("[", " ").replace("]", "");
		castList.setText(castString);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.poster.trimCache();
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

		@Override
		@SuppressWarnings("unchecked")
		protected void onPostExecute(Pair<Integer, Object> result) {
			if (result != null) {
				if (result.first == DetailModel.DETAIL_PRIMARY) {
					DetailActivity.this.detailData = (DetailData) result.second;
					DetailActivity.this
							.setUpDetailData(DetailActivity.this.detailData);
				} else {
					DetailActivity.this.castNames = new ArrayList<String>(
							(ArrayList<String>) result.second);
					DetailActivity.this
							.setUpCastList(DetailActivity.this.castNames);
				}
			}
		}
	}
}
