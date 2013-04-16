package edu.psu.cmpsc483w.moviesearch;

import edu.psu.cmpsc483w.moviesearch.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;

		}

		return true;
	}

	public void openSettings(View v) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void openActorSearch(View v) {
		Intent intent = new Intent(this, ActorSearchActivity.class);
		startActivity(intent);
	}

	public void openMovieSearch(View v) {
		Intent intent = new Intent(this, MovieSearchTemp.class);
		startActivity(intent);
	}

}
