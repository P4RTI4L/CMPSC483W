package edu.psu.cmpsc483w.moviesearch2;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class EndlessAdapter extends BaseAdapter{

	private Context context;
	private CachedDataSource data;
	private View loadingView;
	private View noResultsView;
	
	private View waitingView;
	
	private final static int LOADING_VIEW = 0;
	private final static int CONTENT_VIEW = 1;
	private final static int NO_RESULTS_VIEW = 2;
		
	public EndlessAdapter(Context context, CachedDataSource data, View loadingView, View noResultsView) {
		this.context = context;
		this.data = data;
		
		if (loadingView == null)
		{
			this.loadingView = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		}
		else
		{
			this.loadingView = loadingView;
		}
		
		if (noResultsView == null)
		{
			this.noResultsView = new TextView(context);
			((TextView)this.noResultsView).setText(context.getString(R.string.endless_no_data));
		}
		else
		{
			this.noResultsView = noResultsView;
		}
		
	}
	
	// Overloaded version of constructor that allows a layout identifer for noResultsView to be used
	public EndlessAdapter(Context context, CachedDataSource data, View loadingView, int noResultsLayout) {
		this(context, data, loadingView, 
				((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(noResultsLayout, null));
	}
	
	// Makes changes to the content view as needed, given the relevant data to display
	protected abstract void customiseContentView(View convertView, Object contentData);
	// Initialises the content view for the first time whenever the recycler can't reuse an old view,
	//	should set tags here if planning to use in customiseContentView
	protected abstract View createView(LayoutInflater inflater);
	
	@Override
	public int getCount() {
		int dataCount = data.getDataCount();
		int cachedCount = data.getCachedDataCount();
		
		// More items to load
		if (cachedCount < dataCount) {
			return cachedCount + 1;
		}
		// No items to load
		else if (dataCount == 0) {
			return 1;
		}
		// All items loaded
		else {
			return cachedCount;
		}
	}
	
	public void notifyDataCached()
	{
		waitingView = null;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.isDataCached(position) ? data.getData(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return data.isDataCached(position) ? data.getDataId(position) : 0;
	}
		
	@Override
	public int getViewTypeCount() {
		// 3 types of views, the loading indicator, actual content, and the "no results" view
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (data.getDataCount() == 0) {
			return NO_RESULTS_VIEW;
		}
		else if (position == data.getCachedDataCount()) {
			return LOADING_VIEW;
		}
		else {
			return CONTENT_VIEW;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Figure out what kind of view we're receiving
		int viewType = getItemViewType(position);
		
		if (viewType == LOADING_VIEW) {
			if (waitingView == null) {
				waitingView = loadingView;
				
				new DataTask(this, data).execute(position);
			}
			
			return waitingView;
		}
		else if (viewType == CONTENT_VIEW) {
			
			if (convertView == null) {
				
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				convertView = createView(inflater);
			}
			
			Object contentData = data.getData(position);
			
			customiseContentView(convertView, contentData);
			
			return convertView;
		}
		else {
			return noResultsView;
		}
		
	}
	
	private static class DataTask extends AsyncTask<Integer, Void, Object> {
		EndlessAdapter adapter;
		CachedDataSource source;
		
		protected DataTask(EndlessAdapter adapter, CachedDataSource source) {
			this.adapter = adapter;
			this.source = source;
		}
		
		@Override
		protected Object doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int position = params[0];
			
			return source.getData(position);
		}
		
		@Override
		protected void onPostExecute(Object data) {
			adapter.notifyDataCached();
		}
		
	}
}
