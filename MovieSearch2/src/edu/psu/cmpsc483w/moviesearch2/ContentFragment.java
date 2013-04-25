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
		
		if (savedInstanceState == null)
		{
			topic = new TopicModel(TopicModel.TOPIC_POPULAR);
		}
		
	}
	

	public class TopicContentAdapter extends BaseAdapter {
		
		private Context context;
		private TopicModel dataSource;
		
		public TopicContentAdapter(Context context, TopicModel topic)
		{
			this.context = context;
			this.dataSource = topic;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataSource.getDataCount();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			
			//if (convertView == null)
			//{
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.content_grid_item, null);
			//}
			
			//TextView title = (TextView)convertView.findViewById(R.id.textview_content_grid_item_title);
			//title.setText("Temp");
			
			//return convertView;
			
			// Inefficient, need to make a viewholder
			if (dataSource.isDataCached(position))
			{
				MovieListingData data = (MovieListingData)dataSource.getData(position);
				TextView title = (TextView)convertView.findViewById(R.id.textview_content_grid_item_title);
				title.setText(data.getTitle());
				TextView date = (TextView)convertView.findViewById(R.id.textview_content_grid_item_release_date);
				date.setText(data.getReleaseDate());
				
				return convertView;
			}
			else
			{
				if (waitingView == null)
				{
					ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
					waitingView = progressBar;
					
					new DataTask(this, dataSource).execute(position);
				}
				
				return waitingView;
				
			}
		}
		
	}
	
	private static class DataTask extends AsyncTask<Integer, Void, MovieListingData>
	{
		BaseAdapter adapter;
		TopicModel data;
		
		protected DataTask(BaseAdapter adapter, TopicModel data)
		{
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
		protected void onPostExecute(MovieListingData data)
		{
			waitingView = null;
			this.adapter.notifyDataSetChanged();
		}
		
	}
	
	
}
