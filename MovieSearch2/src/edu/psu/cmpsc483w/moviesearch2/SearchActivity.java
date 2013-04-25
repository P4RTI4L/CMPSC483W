package edu.psu.cmpsc483w.moviesearch2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SearchActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.content, menu);
		
		// Get the spinner for search type and add the custom adapter to it
		Spinner searchType = (Spinner)menu.findItem(R.id.action_search_type).getActionView();
		String[] values = getResources().getStringArray(R.array.actionbar_spinner_values);
		
		ActionbarSpinnerAdapter adapter = new ActionbarSpinnerAdapter(this, R.layout.actionbar_spinner,values);
		searchType.setAdapter(adapter);
		
		return true;
	}
	
	public class ActionbarSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
		
		private Context context;
		private int textViewResourceId;
		private String[] values;
		
		public ActionbarSpinnerAdapter(Context context, int textViewResourceId, String[] values) {
			super(context, textViewResourceId, values);
			
			this.context = context;
			this.textViewResourceId = textViewResourceId;
			this.values = values;
		}
		
		// Create a ViewHolder class to reduce calls to findViewById, since both types just change
		//	a textview, can be reused for both types of custom views
		private class ViewHolder {
			public TextView textViewcontent;
		}
		
		// Define the adapter behavior for the regular view (i.e. the view shown when not expanded)
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			ViewHolder holder;
			
			// If no views to recycle, create a new one
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.actionbar_spinner_view, null);
				holder = new ViewHolder();
				holder.textViewcontent = (TextView)convertView.findViewById(R.id.actionbar_spinner_view_content);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.textViewcontent.setText(values[position]);
			
			return convertView;
		}
		
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater();
			ViewHolder holder;
			
			// If no views to reuse, create a new one
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.actionbar_spinner_dropdown_view, null);
				holder = new ViewHolder();
				holder.textViewcontent = (TextView)convertView.findViewById(R.id.actionbar_spinner_dropdown_content);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.textViewcontent.setText(values[position]);
			
			return convertView;
		}
	}
}
