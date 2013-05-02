package edu.psu.cmpsc483w.moviesearch2;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

public class ContentActivity extends SearchActivity {

	private final static int ACTOR_SUBSEARCH_REQUEST = 0;

	private DualModel dualModel;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_content);

		this.setUpNavigationDropDown();

		if (savedInstanceState == null) {
			this.dualModel = new DualModel("");
			this.query = "";
		} else {
			this.dualModel = savedInstanceState.getParcelable("dual");
			this.query = savedInstanceState.getString("query");
		}
	}

	public void setUpNavigationDropDown() {
		final String[] topics = new String[] { TopicModel.TOPIC_NOW_PLAYING,
				TopicModel.TOPIC_TOP_RATED, TopicModel.TOPIC_POPULAR,
				TopicModel.TOPIC_UPCOMING };

		ArrayAdapter<String> navigationAdapter = new ArrayAdapter<String>(this,
				R.layout.actionbar_spinner_dropdown_view, this.getResources()
						.getStringArray(R.array.actionbar_navigation_values));

		this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		FragmentManager manager = this.getFragmentManager();

		final ContentFragment fragment = (ContentFragment) manager
				.findFragmentById(R.id.main_content_fragment);

		OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				fragment.setTopic(topics[itemPosition]);

				return false;
			}
		};

		this.getActionBar().setListNavigationCallbacks(navigationAdapter,
				navigationListener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_filter_search:
			super.toggleFilterFragment();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putParcelable("dual", this.dualModel);
		savedInstanceState.putString("query", this.query);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		this.setUpSearchAction(menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SearchView searchView = (SearchView) menu.findItem(
				R.id.action_perform_search).getActionView();

		searchView.setQuery(this.query, false);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Look for the request code
		if (requestCode == ACTOR_SUBSEARCH_REQUEST) {
			// Check that it was successful
			if (resultCode == RESULT_OK) {
				ActorData result = data.getParcelableExtra("result");
				this.dualModel.setActorQuery(result);

				this.query = result.getName();
				this.invalidateOptionsMenu();

				Intent intent = new Intent(this, ResultsActivity.class);
				intent.putExtra("dual", this.dualModel);
				intent.putExtra("filter", this.appliedFilter);
				this.startActivity(intent);
			}
		}
	}

	public void setUpSearchAction(Menu menu) {
		final Spinner searchType = (Spinner) menu.findItem(
				R.id.action_search_type).getActionView();

		SearchView searchView = (SearchView) menu.findItem(
				R.id.action_perform_search).getActionView();

		if (this.query != null) {
			searchView.setQuery(this.query, false);
		}

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				if (searchType.getSelectedItemPosition() == SearchActivity.SEARCH_SPINNER_VALUE_CAST) {
					Intent intent = new Intent(ContentActivity.this,
							ActorSubsearchActivity.class);
					intent.putExtra("query", query);
					ContentActivity.this.startActivityForResult(intent,
							ACTOR_SUBSEARCH_REQUEST);
				} else if (searchType.getSelectedItemPosition() == SearchActivity.SEARCH_SPINNER_VALUE_MOVIE) {
					Intent intent = new Intent(ContentActivity.this,
							ResultsActivity.class);
					ContentActivity.this.dualModel.setMovieQuery(query);
					intent.putExtra("dual", ContentActivity.this.dualModel);
					intent.putExtra("filter",
							ContentActivity.this.appliedFilter);

					ContentActivity.this.startActivity(intent);
				}

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				return false;
			}
		});
	}

	@Override
	public void addFilterFragment() {

		super.addFilterFragment();

		FilterFragment filterFragment = FilterFragment
				.newInstance(this.appliedFilter);

		FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();

		transaction.replace(R.id.content_wrapper, filterFragment);
		transaction.addToBackStack(null);

		transaction.commit();
	}
	
	@Override
	public void handleFilterData(Filter filter, ActorSearchModel exclude) {
		super.handleFilterData(filter, exclude);
		
		dualModel.setNewFilter(filter);
		dualModel.setActorSearchModel(exclude);
	}
	
	@Override
	public void fragmentFinished(Filter filter, ActorSearchModel exclude) {
		super.fragmentFinished(filter, exclude);
	}


}
