package edu.psu.cmpsc483w.moviesearch2;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ContentActivity extends SearchActivity {
		
	private final static int ACTOR_SUBSEARCH_REQUEST = 0;
	
	private DualModel dualModel;
	private String query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		
		setUpNavigationDropDown();
		
		if (savedInstanceState == null)
		{
			dualModel = new DualModel("");
			query = "";
		}
		else
		{
			dualModel = savedInstanceState.getParcelable("dual");
			query = savedInstanceState.getString("query");
		}
	}
	
	public void setUpNavigationDropDown()
	{
		final String[] topics = new String[]{TopicModel.TOPIC_NOW_PLAYING, TopicModel.TOPIC_TOP_RATED, 
				TopicModel.TOPIC_POPULAR, TopicModel.TOPIC_UPCOMING};
		
		ArrayAdapter<String> navigationAdapter = new ArrayAdapter<String>(this, R.layout.actionbar_spinner_dropdown_view,
				getResources().getStringArray(R.array.actionbar_navigation_values));
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		FragmentManager manager = getFragmentManager();
		
		final ContentFragment fragment = (ContentFragment) manager.findFragmentById(R.id.main_content_fragment);
		
		OnNavigationListener navigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				fragment.setTopic(topics[itemPosition]);
				
				return false;
			}
		};
		
		getActionBar().setListNavigationCallbacks(navigationAdapter, navigationListener);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_filter_search:
	        	super.addFilterFragment(R.id.content_wrapper);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		
		savedInstanceState.putParcelable("dual", dualModel);
		savedInstanceState.putString("query", query);
		
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		
		final Spinner searchType = (Spinner)menu.findItem(R.id.action_search_type).getActionView();

		SearchView searchView = (SearchView)menu.findItem(R.id.action_perform_search).getActionView();
		
		if (query != null)
		{
			searchView.setQuery(query, false);
		}
		
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (searchType.getSelectedItemPosition() == SearchActivity.SEARCH_SPINNER_VALUE_CAST)
				{
					Intent intent = new Intent(ContentActivity.this, ActorSubsearchActivity.class);
					intent.putExtra("query", query);
					startActivityForResult(intent, ACTOR_SUBSEARCH_REQUEST);
				}
				else if (searchType.getSelectedItemPosition() == SearchActivity.SEARCH_SPINNER_VALUE_MOVIE)
				{
					Intent intent = new Intent(ContentActivity.this, ResultsActivity.class);
					dualModel.setMovieQuery(query);
					intent.putExtra("dual", dualModel);
					
					startActivity(intent);
				}
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				
				return false;
			}
		});
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		SearchView searchView = (SearchView)menu.findItem(R.id.action_perform_search).getActionView();
		
		searchView.setQuery(query, false);
		
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Look for the request code
		if (requestCode == ACTOR_SUBSEARCH_REQUEST) {
			// Check that it was successful
			if (resultCode == RESULT_OK) {
				ActorData result = data.getParcelableExtra("result");
				dualModel.setActorQuery(result.getId());
				
				query = result.getName();
				invalidateOptionsMenu();
				
				Intent intent = new Intent(this, ResultsActivity.class);
				intent.putExtra("dual", dualModel);
				startActivity(intent);
			}
		}
	}
	
	
	
}
