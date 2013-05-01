package edu.psu.cmpsc483w.moviesearch2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

public class ActorSubsearchActivity extends Activity {

	public String query;
	public ActorSubsearchModel subsearchModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_actor_subsearch);

		Intent intent = this.getIntent();

		if (savedInstanceState == null) {
			this.query = intent.getExtras().getString("query");
			this.subsearchModel = new ActorSubsearchModel(this.query);
		} else {
			this.subsearchModel = savedInstanceState.getParcelable("subsearch");
			this.query = savedInstanceState.getString("query");
		}

		GridView actorGridView = (GridView) this
				.findViewById(R.id.gridview_actor_subsearch);
		actorGridView.setAdapter(new ActorSubsearchAdapter(this,
				this.subsearchModel, this.query));

		actorGridView.setOnItemClickListener(new OnItemClickListener() {
			// Send the value back
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("result",
						(ActorData) ActorSubsearchActivity.this.subsearchModel
								.getData(position));
				ActorSubsearchActivity.this.setResult(Activity.RESULT_OK,
						intent);
				ActorSubsearchActivity.this.finish();
			}

		});

		// Show the Up button in the action bar.
		this.setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		this.getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.actor_subsearch, menu);

		SearchView subsearchAction = (SearchView) menu.findItem(
				R.id.action_perform_subsearch).getActionView();

		if (this.query != null) {
			subsearchAction.setQuery(this.query, false);
			subsearchAction.setIconified(false);
		}

		subsearchAction
				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

					@Override
					public boolean onQueryTextSubmit(String query) {

						ActorSubsearchActivity.this.query = query;

						ActorSubsearchActivity.this.subsearchModel = new ActorSubsearchModel(
								query);

						GridView actorGridView = (GridView) ActorSubsearchActivity.this
								.findViewById(R.id.gridview_actor_subsearch);

						actorGridView.setAdapter(new ActorSubsearchAdapter(
								ActorSubsearchActivity.this
										.getApplicationContext(),
								ActorSubsearchActivity.this.subsearchModel,
								query));

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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("subsearch", this.subsearchModel);
		savedInstanceState.putString("query", this.query);

		super.onSaveInstanceState(savedInstanceState);
	}

	// A utility method for generating the empty view since Java requires the
	// first statement in a constructor
	// of a child class to be super()
	public static View generateNoDataView(Context context, String query) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView result = (TextView) inflater.inflate(
				R.layout.actor_subsearch_no_data, null);

		Resources res = context.getResources();
		result.setText(String.format(
				res.getString(R.string.actor_subsearch_no_data), query));

		return result;

	}

	public class ActorSubsearchAdapter extends EndlessAdapter {

		private class ViewHolder {
			TextView name;
		}

		public ActorSubsearchAdapter(Context context, CachedDataSource data,
				String query) {
			super(context, data, null, generateNoDataView(context, query));
		}

		@Override
		protected void customiseContentView(View convertView, Object contentData) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			ActorData actorData = (ActorData) contentData;

			viewHolder.name.setText(actorData.getName());
		}

		@Override
		protected View createView(LayoutInflater inflater) {
			View contentView = inflater.inflate(
					R.layout.actor_subsearch_grid_item, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) contentView
					.findViewById(R.id.textview_actor_subsearch_grid_item_name);

			contentView.setTag(viewHolder);

			return contentView;
		}
	}

}
