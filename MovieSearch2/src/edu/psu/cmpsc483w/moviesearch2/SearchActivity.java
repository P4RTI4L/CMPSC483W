package edu.psu.cmpsc483w.moviesearch2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity implements
		FilterFragment.FilterFragmentReceiver {

	// Indexes of the items in the search type spinner in the action bar, not
	// the best design pattern
	// but the alternatives are either unreliable or too complex to be worth it
	public final static int SEARCH_SPINNER_VALUE_MOVIE = 0;
	public final static int SEARCH_SPINNER_VALUE_CAST = 1;

	// The filter received from pressing the filter action
	protected Filter appliedFilter;
	// Whether the filter fragment is visible or not
	protected boolean isFilterVisible;
	// The index of the search type selected
	protected int searchTypeSelected;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			this.appliedFilter = new Filter();
			this.isFilterVisible = false;
			this.searchTypeSelected = 0;
		} else {
			this.appliedFilter = savedInstanceState.getParcelable("filter");
			this.isFilterVisible = savedInstanceState
					.getBoolean("filterVisible");
			this.searchTypeSelected = savedInstanceState.getInt("searchType");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("filter", this.appliedFilter);
		savedInstanceState.putBoolean("filterVisible", this.isFilterVisible);
		savedInstanceState.putInt("searchType", this.searchTypeSelected);
		
		super.onSaveInstanceState(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.content, menu);

		// Get the spinner for search type and add the custom adapter to it
		Spinner searchType = (Spinner) menu.findItem(R.id.action_search_type)
				.getActionView();
		String[] values = this.getResources().getStringArray(
				R.array.actionbar_spinner_values);
		
		ActionbarSpinnerAdapter adapter = new ActionbarSpinnerAdapter(this,
				R.layout.actionbar_spinner, values);
		searchType.setAdapter(adapter);

		searchType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SearchActivity.this.searchTypeSelected = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		searchType.setSelection(this.searchTypeSelected);
		
		return true;
	}

	/**
	 * Toggles filter visibility
	 */
	public void toggleFilterFragment() {
		if (!this.isFilterVisible) {
			this.addFilterFragment();
		} else {
			this.removeFilterFragment();
		}
	}

	/**
	 * Sets the filter to be visible
	 */
	public void addFilterFragment() {
		this.isFilterVisible = true;
	}

	/**
	 * Sets the filter to be invisible
	 */
	public void removeFilterFragment() {
		this.isFilterVisible = false;
	}

	public class ActionbarSpinnerAdapter extends ArrayAdapter<String> implements
			SpinnerAdapter {

		@SuppressWarnings("unused")
		private Context context;
		@SuppressWarnings("unused")
		private int textViewResourceId;
		private String[] values;

		public ActionbarSpinnerAdapter(Context context, int textViewResourceId,
				String[] values) {
			super(context, textViewResourceId, values);

			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.values = values;
		}

		// Create a ViewHolder class to reduce calls to findViewById, since both
		// types just change
		// a textview, can be reused for both types of custom views
		private class ViewHolder {
			public TextView textViewcontent;
		}

		// Define the adapter behavior for the regular view (i.e. the view shown
		// when not expanded)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
			ViewHolder holder;

			// If no views to recycle, create a new one
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.actionbar_spinner_view,
						null);
				holder = new ViewHolder();
				holder.textViewcontent = (TextView) convertView
						.findViewById(R.id.actionbar_spinner_view_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.textViewcontent.setText(this.values[position]);

			return convertView;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getDropDownView(int,
		 * android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
			ViewHolder holder;

			// If no views to reuse, create a new one
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.actionbar_spinner_dropdown_view, null);
				holder = new ViewHolder();
				holder.textViewcontent = (TextView) convertView
						.findViewById(R.id.actionbar_spinner_dropdown_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.textViewcontent.setText(this.values[position]);

			return convertView;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.psu.cmpsc483w.moviesearch2.FilterFragment.FilterFragmentReceiver#
	 * handleFilterData(edu.psu.cmpsc483w.moviesearch2.Filter,
	 * edu.psu.cmpsc483w.moviesearch2.ActorSearchModel)
	 */
	@Override
	public void handleFilterData(Filter filter, ActorSearchModel exclude) {
		// TODO Auto-generated method stub
		this.appliedFilter = filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.psu.cmpsc483w.moviesearch2.FilterFragment.FilterFragmentReceiver#
	 * removeFragment(edu.psu.cmpsc483w.moviesearch2.Filter,
	 * edu.psu.cmpsc483w.moviesearch2.ActorSearchModel)
	 */
	@Override
	public void fragmentFinished(Filter filter, ActorSearchModel exclude) {
		this.handleFilterData(filter, exclude);
		this.removeFilterFragment();
	}
	
	@Override
	public void onBackPressed() {

		if (this.isFilterVisible) {
			this.removeFilterFragment();
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public void startSubsearchActivity(String query) {
		Intent intent = new Intent(SearchActivity.this,
				ActorSubsearchActivity.class);
		intent.putExtra("query", query);
		SearchActivity.this.startActivityForResult(intent,
				FilterFragment.FILTER_FRAGMENT_SUBSEARCH_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Look for the request code
		if (requestCode == FilterFragment.FILTER_FRAGMENT_SUBSEARCH_REQUEST) {
			// Check that it was successful
			if (resultCode == RESULT_OK) {
				ActorData result = data.getParcelableExtra("result");
				FilterFragment filterFragment = (FilterFragment) getFragmentManager().
						findFragmentByTag(FilterFragment.TAG);
				
				if (filterFragment != null) {
					filterFragment.addActorExcludeData(result);
				}
			}
		}
	}

}
