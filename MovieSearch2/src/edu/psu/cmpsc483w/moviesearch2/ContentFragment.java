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
	

	public class TopicContentAdapter extends EndlessAdapter {
		
		private class ViewHolder
		{
			TextView title;
			TextView date;
		}
		
		public TopicContentAdapter(Context context, CachedDataSource data) {
			super(context, data, null, R.layout.topic_no_data);
		}

		@Override
		protected void customiseContentView(View convertView, Object contentData) {
			ViewHolder viewHolder = (ViewHolder)convertView.getTag();
			
			MovieListingData movieData = (MovieListingData)contentData;
			
			viewHolder.title.setText(movieData.getTitle());
			viewHolder.date.setText(movieData.getReleaseDate());
		}

		@Override
		protected View createView(LayoutInflater inflater) {
			View contentView = inflater.inflate(R.layout.content_grid_item, null);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.title = (TextView)contentView.findViewById(R.id.textview_content_grid_item_title);
			viewHolder.date = (TextView)contentView.findViewById(R.id.textview_content_grid_item_release_date);
			
			contentView.setTag(viewHolder);
			
			return contentView;
		}
	}
	
}
