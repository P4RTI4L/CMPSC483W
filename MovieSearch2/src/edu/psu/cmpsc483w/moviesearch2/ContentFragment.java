package edu.psu.cmpsc483w.moviesearch2;

import java.io.File;
import java.io.IOException;
import java.net.ResponseCache;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContentFragment extends Fragment {

	private TopicModel topic;
	private ImageModel image;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content, container,
				false);

		GridView contentGrid = (GridView) view
				.findViewById(R.id.gridview_content);
		contentGrid.setAdapter(new TopicContentAdapter(getActivity()
				.getApplicationContext(), topic));

		contentGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MovieListingData movieListingData = (MovieListingData) topic.getData(position);
	
				Intent intent = new Intent (getActivity ().getApplicationContext (), DetailActivity.class);
				intent.putExtra("movieId", movieListingData.getId ());
				startActivity (intent);				
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			topic = new TopicModel(TopicModel.TOPIC_NOW_PLAYING);
			image = new ImageModel(getActivity().getApplicationContext(), R.drawable.film_reel);
		} else {
			topic = savedInstanceState.getParcelable("topic");
			image = new ImageModel(getActivity().getApplicationContext(), R.drawable.film_reel);

		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("topic", topic);

		super.onSaveInstanceState(savedInstanceState);
	}

	public void setTopic(String newTopic)
	{
		topic.setNewTopic(newTopic);
		
		GridView contentView = (GridView)getActivity().findViewById(R.id.gridview_content);
		TopicContentAdapter adapter = (TopicContentAdapter) contentView.getAdapter();
		adapter.notifyDataSetChanged();
	}
	
	public class TopicContentAdapter extends EndlessAdapter {

		private class ViewHolder {
			TextView title;
			TextView date;
			ImageView icon;
		}

		public TopicContentAdapter(Context context, CachedDataSource data) {
			super(context, data, null, R.layout.topic_no_data);
		}

		@Override
		protected void customiseContentView(View convertView, Object contentData) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();

			MovieListingData movieData = (MovieListingData) contentData;

			viewHolder.title.setText(movieData.getTitle());
			viewHolder.date.setText(movieData.getReleaseDate());
			
			// Replace it with the no image resource temporarily (or permanently if there is none)
			
			image.setImageViewWithUrl(viewHolder.icon, movieData.getPosterPath(), TmdbModel.POSTER_IMAGE);
			
			/*viewHolder.icon.setImageResource(R.drawable.film_reel);
			
			// If the view previously had an asynctask and that id isn't the same, cancel it
			if (viewHolder.async != null && viewHolder.async.id != movieData.getId())
			{
				viewHolder.async.cancel(true);
			}
			
			AsyncTaskImageQuery task = new AsyncTaskImageQuery(viewHolder.icon, movieData.getId());
			viewHolder.async = task;
			
			if (!movieData.getPosterPath().equals(""))
			{
				task.execute(movieData.getPosterPath(),"200");
			}*/
				
			
			
			//new AsyncTaskImageQuery(viewHolder.icon, movieData.getId()).execute(movieData.getPosterPath(),"200");
		}

		@Override
		protected View createView(LayoutInflater inflater) {
			View contentView = inflater.inflate(R.layout.content_grid_item,
					null);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.title = (TextView) contentView
					.findViewById(R.id.textview_content_grid_item_title);
			viewHolder.date = (TextView) contentView
					.findViewById(R.id.textview_content_grid_item_release_date);
			viewHolder.icon = (ImageView) contentView.findViewById(R.id.imageview_content_grid_item);
			
			contentView.setTag(viewHolder);

			return contentView;
		}
	}
	
	// Web queries should always be performed asynchronously to prevent blocking
		// the UI thread, for this purpose
		// an AsyncTask is needed to update the interface as data is ready
		/*private class AsyncTaskImageQuery extends
				AsyncTask<String, Void, Bitmap> {
			
			private ImageView imageView;
			public int id;
			
			public AsyncTaskImageQuery(ImageView image, int id)
			{
				imageView = image;
				this.id = id;
			}
			@Override
			protected Bitmap doInBackground(String... params) {

				String url = params[0];
				String width = params[1];
				
				return ImageModel.synchronousTmdbRelativeImageDownload(url, TmdbModel.POSTER_IMAGE, Integer.valueOf(width));

			}

			protected void onPostExecute(Bitmap result) {
				if (result != null)
				{
					imageView.setImageBitmap(result);
				}
			}
		}*/

}
