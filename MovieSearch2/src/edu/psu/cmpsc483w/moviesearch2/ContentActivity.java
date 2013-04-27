package edu.psu.cmpsc483w.moviesearch2;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
				// TODO Auto-generated method stub

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
				// TODO Auto-generated method stub
				
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
