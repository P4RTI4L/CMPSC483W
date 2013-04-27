package edu.psu.cmpsc483w.moviesearch2;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContentFragment extends Fragment {

	private TopicModel topic;
	private static View waitingView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content, container, false);
		
		GridView contentGrid = (GridView)view.findViewById(R.id.gridview_content);
		contentGrid.setAdapter(new TopicContentAdapter(getActivity().getApplicationContext(), topic));
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			topic = new TopicModel(TopicModel.TOPIC_POPULAR);
		}
		else {
			topic = savedInstanceState.getParcelable("topic");
		}
		
	}
	
	@Override
	public void onSaveInstanceState (Bundle savedInstanceState) {
		savedInstanceState.putParcelable("topic", topic);
		
		super.onSaveInstanceState(savedInstanceState);
	}
	

	public class TopicContentAdapter extends BaseAdapter {
		
		private Context context;
		private TopicModel dataSource;
		
		private final static int LOADING_VIEW = 0;
		private final static int CONTENT_VIEW = 1;
		private final static int NO_RESULTS_VIEW = 2;
		
		public TopicContentAdapter(Context context, TopicModel topic) {
			this.context = context;
			this.dataSource = topic;
		}
		
		private class ViewHolder {
			TextView title;
			TextView date;
		}
		
		@Override
		public int getCount() {
			int dataCount = dataSource.getDataCount();
			int cachedCount = dataSource.getCachedDataCount();
			
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

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataSource.isDataCached(position) ? dataSource.getData(position) : null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return dataSource.isDataCached(position) ? dataSource.getDataId(position) : 0;
		}
		
		@Override
		public int getViewTypeCount() {
			// 3 types of views, the loading indicator, actual content, and the "no results" view
			return 3;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (dataSource.getDataCount() == 0) {
				return NO_RESULTS_VIEW;
			}
			else if (position == dataSource.getCachedDataCount()) {
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
					ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
					waitingView = progressBar;
					
					new DataTask(this, dataSource).execute(position);
				}
				
				return waitingView;
			}
			else if (viewType == CONTENT_VIEW) {
				ViewHolder viewHolder;
				
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.content_grid_item, null);
					
					viewHolder = new ViewHolder();
					
					viewHolder.title = (TextView)convertView.findViewById(R.id.textview_content_grid_item_title);
					viewHolder.date = (TextView)convertView.findViewById(R.id.textview_content_grid_item_release_date);
					
					convertView.setTag(viewHolder);
				}
				else {
					viewHolder = (ViewHolder)convertView.getTag();
				}
				
				MovieListingData data = (MovieListingData)dataSource.getData(position);
				
				viewHolder.title.setText(data.getTitle());
				viewHolder.date.setText(data.getReleaseDate());
				
				return convertView;
			}
			else {
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.topic_no_data, null);
				}
				
				return convertView;
			}
			
		}
		
	}
	
	private static class DataTask extends AsyncTask<Integer, Void, MovieListingData> {
		BaseAdapter adapter;
		TopicModel data;
		
		protected DataTask(BaseAdapter adapter, TopicModel data) {
			this.adapter = adapter;
			this.data = data;
		}
		
		@Override
		protected MovieListingData doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int position = params[0];
			
			MovieListingData result = (MovieListingData)data.getData(position);
			
			return result;
		}
		
		@Override
		protected void onPostExecute(MovieListingData data) {
			waitingView = null;
			this.adapter.notifyDataSetChanged();
		}
		
	}
	
	
}
