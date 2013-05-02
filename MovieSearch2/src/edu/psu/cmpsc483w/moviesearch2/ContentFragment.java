package edu.psu.cmpsc483w.moviesearch2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
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
		contentGrid.setAdapter(new TopicContentAdapter(this.getActivity()
				.getApplicationContext(), this.topic));

		contentGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MovieListingData movieListingData = (MovieListingData) ContentFragment.this.topic
						.getData(position);

				Intent intent = new Intent(ContentFragment.this.getActivity()
						.getApplicationContext(), DetailActivity.class);
				intent.putExtra("movieId", movieListingData.getId());
				ContentFragment.this.startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			this.topic = new TopicModel(TopicModel.TOPIC_NOW_PLAYING);
		} else {
			this.topic = savedInstanceState.getParcelable("topic");
			
		}
		this.image = new ImageModel(this.getActivity()
				.getApplicationContext(), R.drawable.film_reel);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("topic", this.topic);

		super.onSaveInstanceState(savedInstanceState);
	}

	public void setTopic(String newTopic) {
		this.topic.setNewTopic(newTopic);

		GridView contentView = (GridView) this.getActivity().findViewById(
				R.id.gridview_content);
		TopicContentAdapter adapter = (TopicContentAdapter) contentView
				.getAdapter();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.image.trimCache();
	}

	public class TopicContentAdapter extends EndlessAdapter {

		private class ViewHolder {
			TextView title;
			TextView date;
			ImageView icon;
			RatingBar rating;
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
			
			if (movieData.getVoteCount() > 0) {
				viewHolder.rating.setVisibility(View.VISIBLE);
				viewHolder.rating.setRating((float) (movieData.getRating()/2));
			} else {
				viewHolder.rating.setVisibility(View.GONE);
			}
			// Replace it with the no image resource temporarily (or permanently
			// if there is none)

			ContentFragment.this.image.setImageViewWithUrl(viewHolder.icon,
					movieData.getPosterPath(), TmdbModel.POSTER_IMAGE);
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
			viewHolder.icon = (ImageView) contentView
					.findViewById(R.id.imageview_content_grid_item);
			viewHolder.rating = (RatingBar) contentView
					.findViewById(R.id.ratingBar_movie_rating);
			contentView.setTag(viewHolder);

			return contentView;
		}
	}

}
