package edu.psu.cmpsc483w.moviesearch2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ResultsActivity extends SearchActivity {

	private DualModel dualModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_results);
		// Show the Up button in the action bar.
		this.setupActionBar();

		if (savedInstanceState == null) {
			Intent intent = this.getIntent();
			this.dualModel = intent.getParcelableExtra("dual");
			this.appliedFilter = intent.getParcelableExtra("filter");
		} else {
			this.dualModel = savedInstanceState.getParcelable("dual");
			this.appliedFilter = savedInstanceState.getParcelable("filter");
		}

		GridView gridView = (GridView) this.findViewById(R.id.gridview_results);
		gridView.setAdapter(new ResultsAdapter(this, this.dualModel));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				MovieListingData movieListingData = (MovieListingData) ResultsActivity.this.dualModel
						.getData(position);

				Intent intent = new Intent(ResultsActivity.this
						.getApplicationContext(), DetailActivity.class);
				intent.putExtra("movieId", movieListingData.getId());
				ResultsActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("dual", this.dualModel);
		savedInstanceState.putParcelable("filter", this.appliedFilter);
		
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
		public int getCount() {
			int dataCount = dualModel.getDataCount();
			int cachedCount = dualModel.getCachedDataCount();
			int filteredCount = dualModel.getFilteredDataCount();

			// More items to load
			if (cachedCount < dataCount) {
				return filteredCount + 1;
			}
			// No items to load
			else if (filteredCount == 0) {
				return 1;
			}
			// All items loaded
			else {
				return filteredCount;
			}
		}

		@Override
		public int getItemViewType(int position) {
			if ((dualModel.getCachedDataCount() == dualModel.getDataCount())
					&& dualModel.getFilteredDataCount() == 0) {
				return NO_RESULTS_VIEW;
			} else if (position == dualModel.getFilteredDataCount()) {
				return LOADING_VIEW;
			} else {
				return CONTENT_VIEW;
			}
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
		
		@Override
		public void notifyDataCached() {
			dualModel.reapplyFilter();
			super.notifyDataCached();
		}

	}

	@Override
	public void addFilterFragment() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFilterFragment() {
		// TODO Auto-generated method stub

	}

}
