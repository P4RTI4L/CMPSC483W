package edu.psu.mjc5606.moviesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.Build;

public class ActorSubsearchActivity extends Activity {

	private JSONObject lastRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actor_subsearch);
		
		ListView list = (ListView)findViewById(R.id.listView);
		
		registerForContextMenu(list);
		final Context context = this;
		list.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					JSONArray results = lastRequest.getJSONArray("results");
					JSONObject data = results.getJSONObject(arg2);
					AlertDialog.Builder builder = new AlertDialog.Builder(context)
					;
					builder.setTitle("Confirm selection");
					builder.setMessage("Add actor '"+data.getString("name")+"'?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					builder.create().show();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			
		});
		// Show the Up button in the action bar.
		setupActionBar();
		setupQueryTextListener();
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
		getMenuInflater().inflate(R.menu.actor_subsearch, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu,v,menuInfo);
		getMenuInflater().inflate(R.menu.subsearch_context_menu, menu);
	}
	
	// Sets up the searchview queryTextListener
	public void setupQueryTextListener()
	{
		SearchView searchview = (SearchView)findViewById(R.id.searchView1);
		searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				
				// Perform the search
				new AsyncTaskActorQuery().execute(query);
				// Dismiss the keyboard
				SearchView sv = (SearchView)findViewById(R.id.searchView1);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(sv.getWindowToken(),0);
				
				return true;
			}
			
			
		});
	}
	
	// Web queries should always be performed asynchronously to prevent blocking the UI thread, for this purpose
	//	an AsyncTask is needed to update the interface as data is ready
	private class AsyncTaskActorQuery extends AsyncTask<String, Integer, JSONObject>
	{
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			String nameSubstring = params[0];

			if (params.length > 1)
			{
				String page = params[1];
				return ActorSubsearchModel.synchronousActorSearch(nameSubstring, page);
			}
			
			return ActorSubsearchModel.synchronousActorSearch(nameSubstring);
		}
		
		@Override
		protected void onPostExecute(JSONObject result)
		{
			try {
				// Copy it to the activity for later use
				lastRequest = result;
				// Get a list of the names and populate the list
				JSONArray actorList = result.getJSONArray("results");
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i<actorList.length(); i++)
				{	
					JSONObject entry = (JSONObject) actorList.get(i);
					names.add(entry.getString("name"));
				}
				ListView listView = (ListView)findViewById(R.id.listView);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.actor_subsearch_row, names);
				Log.i("Test", "Length: "+names.size());
				listView.setAdapter(adapter);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}
