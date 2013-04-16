package edu.psu.cmpsc483w.moviesearch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

public class MovieSearchTemp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_search_temp);
		setupQueryTextListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.movie_search_temp, menu);
		return true;
	}
	
	// Sets up the searchview queryTextListener
		public void setupQueryTextListener()
		{
			SearchView searchview = (SearchView)findViewById(R.id.movie_searchView);
			searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

				@Override
				public boolean onQueryTextChange(String newText) {
					return true;
				}

				@Override
				public boolean onQueryTextSubmit(String query) {
					
					Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
					intent.putExtra("type", "movie");
					intent.putExtra("query", query);
					startActivity(intent);
					
					return true;
				}
				
				
			});
		}

}
