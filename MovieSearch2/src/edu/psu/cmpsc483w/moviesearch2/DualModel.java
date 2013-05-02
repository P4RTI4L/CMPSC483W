package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DualModel implements CachedDataSource, Parcelable {

	private final static int MOVIES_ACTIVE = 1;
	private final static int CAST_ACTIVE = 2;

	private int activeType;

	private ActorSearchModel actorModel;
	private MovieSearchModel movieModel;

	private Filter filter;
	
	private ArrayList<MovieListingData> filteredResults;
	
	public DualModel(ActorData actorQuery) {
		this.activeType = CAST_ACTIVE;
		this.actorModel = new ActorSearchModel(actorQuery);
		this.movieModel = new MovieSearchModel("");
		this.filter = new Filter();
		this.filteredResults = new ArrayList<MovieListingData>();
	}

	public DualModel(ActorSearchModel actorModel) {
		this.activeType = CAST_ACTIVE;
		this.actorModel = actorModel;
		this.movieModel = new MovieSearchModel("");
		this.filter = new Filter();
		this.filteredResults =  new ArrayList<MovieListingData>();
	}

	public DualModel(MovieSearchModel movieModel) {
		this.activeType = MOVIES_ACTIVE;
		this.movieModel = movieModel;
		this.actorModel = new ActorSearchModel();
		this.filter = new Filter();
		this.filteredResults = new ArrayList<MovieListingData>();
	}

	public DualModel(String movieQuery) {
		this.activeType = MOVIES_ACTIVE;
		this.movieModel = new MovieSearchModel(movieQuery);
		this.actorModel = new ActorSearchModel();
		this.filter = new Filter();
		this.filteredResults = new ArrayList<MovieListingData>();
	}

	public DualModel(Parcel in) {
		this.activeType = in.readInt();
		this.actorModel = in.readParcelable(ActorSearchModel.class
				.getClassLoader());
		this.movieModel = in.readParcelable(MovieSearchModel.class
				.getClassLoader());
		this.filter = in.readParcelable(Filter.class.getClassLoader());
		this.filteredResults = new ArrayList<MovieListingData>();
		in.readList(this.filteredResults, null);
	}

	public void setMovieQuery(String movieQuery) {
		if (this.activeType == MOVIES_ACTIVE) {
			this.movieModel.setQuery(movieQuery);
		} else if (this.activeType == CAST_ACTIVE) {
			this.activeType = MOVIES_ACTIVE;
			this.actorModel.clearModel();
			this.movieModel.setQuery(movieQuery);
		}
		this.filteredResults.clear();
	}

	public void setActorQuery(ActorData actorQuery) {
		if (this.activeType == MOVIES_ACTIVE) {
			this.activeType = CAST_ACTIVE;
			this.movieModel.clearData();
			this.actorModel.setQueryActor(actorQuery);
		} else if (this.activeType == CAST_ACTIVE) {
			this.actorModel.setQueryActor(actorQuery);
		}
		this.filteredResults.clear();
	}

	public void addExcludeActor(ActorData actorExclude) {
		this.actorModel.addExcludeActor(actorExclude);
	}

	public void removeExcludeActor(ActorData actorExclude) {
		this.actorModel.removeExcludeActor(actorExclude);
	}

	@Override
	public boolean isDataCached(int position) {
		return this.activeType == MOVIES_ACTIVE ? this.movieModel
				.isDataCached(position) : this.actorModel
				.isDataCached(position);
	}

	@Override
	public int getDataCount() {
		return this.activeType == MOVIES_ACTIVE ? this.movieModel
				.getDataCount() : this.actorModel.getDataCount();
	}

	@Override
	public int getCachedDataCount() {
		return this.activeType == MOVIES_ACTIVE ? this.movieModel
				.getCachedDataCount() : this.actorModel.getCachedDataCount();
	}

	public int getFilteredDataCount() {
		return this.filteredResults.size();
	}
	
	@Override
	public Object getData(int position) {
		if (position >= filteredResults.size())
		{
			if (this.activeType == MOVIES_ACTIVE) {
				return this.movieModel.getData(this.movieModel.getCachedDataCount());
			}
			// Shouldn't happen, return something reasonable
			else {
				return this.actorModel.getData(position);
			}
		}
		else
		{
			return this.filteredResults.get(position);
		}
	}

	@Override
	public long getDataId(int position) {
		if (position >= filteredResults.size())
		{
			if (this.activeType == MOVIES_ACTIVE) {
				return this.movieModel.getDataId(this.movieModel.getCachedDataCount());
			}
			// Shouldn't happen, return something reasonable
			else {
				return this.actorModel.getDataId(position);
			}
		}
		else
		{
			return this.filteredResults.get(position).getId();
		}
	}
	
	public void reapplyFilter() {
		if (this.activeType == MOVIES_ACTIVE) {
			this.filteredResults = this.movieModel.getFilteredData(filter);
		} else {
			this.filteredResults = this.actorModel.getFilteredData(filter);
		}
	}
	
	public void setNewFilter(Filter filter)
	{
		this.filter = filter;
		reapplyFilter();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public ActorSearchModel getActorSearchModel() {
		return this.actorModel;
	}
	
	public void setActorSearchModel(ActorSearchModel actor) {
		this.actorModel = actor;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.activeType);
		out.writeParcelable(this.actorModel, flags);
		out.writeParcelable(this.movieModel, flags);
		out.writeParcelable(this.filter, flags);
		out.writeList(this.filteredResults);
	}

	// All Parcelables MUST have a CREATOR
	public static final Parcelable.Creator<DualModel> CREATOR = new Parcelable.Creator<DualModel>() {
		// Create a DetailData object from a Parcel
		@Override
		public DualModel createFromParcel(Parcel source) {
			return new DualModel(source);
		}

		@Override
		public DualModel[] newArray(int size) {
			return new DualModel[size];
		}

	};
}
