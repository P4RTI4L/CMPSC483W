package edu.psu.cmpsc483w.moviesearch2;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class ResultsActivity extends SearchActivity {

	private DualModel dualModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		// Show the Up button in the action bar.
		setupActionBar();

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			dualModel = intent.getParcelableExtra("dual");
		} else {
			dualModel = savedInstanceState.getParcelable("dual");
		}

		GridView gridView = (GridView) findViewById(R.id.gridview_results);
		gridView.setAdapter(new ResultsAdapter(this, dualModel));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dualModel.moviesActive())
				{
					MovieListingData movieListingData = (MovieListingData) dualModel
							.getData(position);
	
					Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
					intent.putExtra("movieId", movieListingData.getId());
					startActivity(intent);
				}
				else
				{
					ActorData actorData = (ActorData) dualModel.getData (position);
					
					// TODO: Actor details
					// Intent intent = new Intent (getApplicationContext (), ???.class);
					// Intent.putExtra (???, actorData.getId ());
					// startActivity(intent);
				}
				
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("dual", dualModel);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ResultsAdapter extends EndlessAdapter {

		private class ViewHolder {
			TextView title;
			TextView date;
		}

		public ResultsAdapter(Context context, CachedDataSource data) {
			super(context, data, null, null);
		}

		@Override
		protected void customiseContentView(View convertView, Object contentData) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();

			MovieListingData movieData = (MovieListingData) contentData;

			viewHolder.title.setText(movieData.getTitle());
			viewHolder.date.setText(movieData.getReleaseDate());
		}

		@Override
		protected View createView(LayoutInflater inflater) {
			View contentView = inflater.inflate(R.layout.content_grid_item,
					null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.title = (TextView) contentView
					.findViewById(R.id.textview_content_grid_item_title);
			viewHolder.date = (TextView) contentView
					.findViewById(R.id.textview_content_grid_item_release_date);

			contentView.setTag(viewHolder);

			return contentView;
		}
	}

}
