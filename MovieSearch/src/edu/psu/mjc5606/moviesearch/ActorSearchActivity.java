package edu.psu.mjc5606.moviesearch;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

public class ActorSearchActivity extends Activity {

	private final static int SUBSEARCH_ID_SEARCH = 0;
	private final static int SUBSEARCH_ID_EXCLUDE = 1;
	
	// TODO: need to ensure that the lists are mutually exclusive
	private ArrayList<ActorData> searchActors, excludeActors;
	private ArrayList<String> searchNames, excludeNames;
	
	private ActorSearchArrayAdapter searchAdapter, excludeAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actor_search);
		// Show the Up button in the action bar.
		setupActionBar();
		
		if (savedInstanceState != null)
		{
			searchActors = savedInstanceState.getParcelableArrayList("searchActors");
			excludeActors = savedInstanceState.getParcelableArrayList("excludeActors");
			searchNames = savedInstanceState.getStringArrayList("searchNames");
			excludeNames = savedInstanceState.getStringArrayList("excludeNames");
		}
		else
		{
			// Initialize the ArrayLists
			searchActors = new ArrayList<ActorData>();
			excludeActors = new ArrayList<ActorData>();
			searchNames = new ArrayList<String>();
			excludeNames = new ArrayList<String>();
		}
		
		// Initialize the listview adapters
		ListView searchList = (ListView) findViewById(R.id.actor_search_list);
		ListView excludeList = (ListView) findViewById(R.id.actor_exclude_list);
		
		searchAdapter = new ActorSearchArrayAdapter(this, searchNames);
		excludeAdapter = new ActorSearchArrayAdapter(this, excludeNames);
		
		searchList.setAdapter(searchAdapter);
		excludeList.setAdapter(excludeAdapter);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actor_search, menu);
		return true;
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
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

	    savedInstanceState.putParcelableArrayList("searchActors", searchActors);
	    savedInstanceState.putParcelableArrayList("excludeActors", excludeActors);
	    savedInstanceState.putStringArrayList("searchNames", searchNames);
	    savedInstanceState.putStringArrayList("excludeNames", excludeNames);
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}

	public void openSubsearch(View v)
	{
		Intent intent = new Intent(this, ActorSubsearchActivity.class);
		// Depending on which button was pressed, pass an id so the activity can identify which
		// list to add the result to
		startActivityForResult(intent, 
				v.getId() == R.id.search_add_button ? SUBSEARCH_ID_SEARCH : SUBSEARCH_ID_EXCLUDE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode)
		{
			// Process the data from the subsearch activity (for the search list)
			case SUBSEARCH_ID_SEARCH:
				// Check if it succeeded or the search was canceled
				if (resultCode == Activity.RESULT_OK)
				{
					ActorData resultData = data.getParcelableExtra("actor_data");
					searchActors.add(resultData);
					searchNames.add(resultData.getName());
					searchAdapter.notifyDataSetChanged();
				}
				break;
				// Process the data from the subsearch activity (for the exclude list)
				case SUBSEARCH_ID_EXCLUDE:
					// Check if it succeeded or the search was canceled
					if (resultCode == Activity.RESULT_OK)
					{
						ActorData resultData = data.getParcelableExtra("actor_data");
						excludeActors.add(resultData);
						excludeNames.add(resultData.getName());
						excludeAdapter.notifyDataSetChanged();
					}
					break;
		}
	}
	
	// Custom adapter for the actor names ListView
	private class ActorSearchArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final ArrayList<String> names;
		
		// ViewHolder pattern that reduces calls to findViewById for performance gains when
		// reusing views
		private class ViewHolder {
			public TextView text;
			public Button remove;
		}
		
		public ActorSearchArrayAdapter(Activity context, ArrayList<String> names)
		{
			super(context, R.layout.actor_search_row, names);
			this.context = context;
			this.names = names;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View rowView = convertView;
			
			// No row views available for reuse, so make a new one
			if (rowView == null)
			{
				rowView = context.getLayoutInflater().inflate(R.layout.actor_search_row, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) rowView.findViewById(R.id.search_row_name);
				viewHolder.remove = (Button) rowView.findViewById(R.id.search_row_remove);
				// Set the tag for when it gets reused
				rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
			final String name = names.get(position);
			holder.text.setText(name);	
		
			final ActorSearchArrayAdapter self = this;
			// Make a dialog appear when clicking the remove button
			holder.remove.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Confirm selection");
					builder.setMessage("Remove actor '"+name+"'?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							names.remove(position);
							self.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
						}
					});
					builder.create().show();
	
				}
			});
			
			return rowView;
		}
	}
	
	
}
