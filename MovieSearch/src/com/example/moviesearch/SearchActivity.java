package com.example.moviesearch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				  this, R.array.search_array, android.R.layout.simple_spinner_item );
				adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

}
