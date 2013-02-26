package com.example.moviesearch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ImageButton searchButton = (ImageButton) findViewById (R.id.searchButton);
        searchButton.setOnClickListener (new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
		    	Intent i = new Intent (v.getContext (), SearchActivity.class);
		    	startActivity (i);
			}
		});
        
        ImageButton settingsButton = (ImageButton) findViewById (R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent (v.getContext(), SettingsActivity.class);
				startActivity (i);
			}
		});
        
        ImageButton actorSearchButton = (ImageButton) findViewById (R.id.actorSearchButton);
        actorSearchButton.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent (v.getContext(), ActorSearchActivity.class);
				startActivity (i);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
