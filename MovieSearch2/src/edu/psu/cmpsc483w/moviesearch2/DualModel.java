package edu.psu.cmpsc483w.moviesearch2;

import android.os.Parcel;
import android.os.Parcelable;

public class DualModel implements CachedDataSource, Parcelable {
	
	private final static int MOVIES_ACTIVE = 0;
	private final static int CAST_ACTIVE = 0;
	
	private int activeType;
	
	private ActorSearchModel actorModel;
	private MovieSearchModel movieModel;
	
	public DualModel(int actorQuery)
	{
		this.activeType = CAST_ACTIVE;
		this.actorModel = new ActorSearchModel(actorQuery);
		this.movieModel = new MovieSearchModel("");
	}
	
	public DualModel(ActorSearchModel actorModel)
	{
		this.activeType = CAST_ACTIVE;
		this.actorModel = actorModel;
		this.movieModel = new MovieSearchModel("");
	}
	
	public DualModel(MovieSearchModel movieModel)
	{
		this.activeType = MOVIES_ACTIVE;
		this.movieModel = movieModel;
		this.actorModel = new ActorSearchModel();
	}
	
	public DualModel(String movieQuery)
	{
		this.activeType = MOVIES_ACTIVE;
		this.movieModel = new MovieSearchModel(movieQuery);
		this.actorModel = new ActorSearchModel();
	}
	
	public DualModel(Parcel in)
	{
		this.activeType = in.readInt();
		this.actorModel = in.readParcelable(null);
		this.movieModel = in.readParcelable(null);
	}

	public void setMovieQuery(String movieQuery)
	{
		if (this.activeType == MOVIES_ACTIVE)
		{
			this.movieModel.setQuery(movieQuery);
		}
		else if (this.activeType == CAST_ACTIVE)
		{
			this.activeType = MOVIES_ACTIVE;
			this.actorModel.clearModel();
			this.movieModel.setQuery(movieQuery);
		}
	}
	
	public void setActorQuery(int actorQueryId)
	{
		if (this.activeType == MOVIES_ACTIVE)
		{
			this.activeType = CAST_ACTIVE;
			this.movieModel.clearData();
			this.actorModel.setQueryActor(actorQueryId);
		}
		else if (this.activeType == CAST_ACTIVE)
		{
			this.actorModel.setQueryActor(actorQueryId);
		}
	}
	
	public void addExcludeActor(int actorExcludeId)
	{
		this.actorModel.addExcludeActor(actorExcludeId);
	}
	
	public void removeExcludeActor(int actorExcludeId)
	{
		this.actorModel.removeExcludeActor(actorExcludeId);
	}
	
	@Override
	public boolean isDataCached(int position) {
		// TODO Auto-generated method stub
		return this.activeType == MOVIES_ACTIVE ? 
				this.movieModel.isDataCached(position) : this.actorModel.isDataCached(position);
	}

	@Override
	public int getDataCount() {
		// TODO Auto-generated method stub
		return this.activeType == MOVIES_ACTIVE ?
				this.movieModel.getDataCount() : this.actorModel.getDataCount();
	}

	@Override
	public int getCachedDataCount() {
		// TODO Auto-generated method stub
		return this.activeType == MOVIES_ACTIVE ?
				this.movieModel.getCachedDataCount() : this.actorModel.getCachedDataCount();
	}

	@Override
	public Object getData(int position) {
		// TODO Auto-generated method stub
		return this.activeType == MOVIES_ACTIVE ?
				this.movieModel.getData(position) : this.actorModel.getData(position);
	}

	@Override
	public long getDataId(int position) {
		// TODO Auto-generated method stub
		return this.activeType == MOVIES_ACTIVE ?
				this.movieModel.getDataId(position) : this.actorModel.getDataId(position);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(this.activeType);
		out.writeParcelable(this.actorModel, flags);
		out.writeParcelable(this.movieModel, flags);
	}
	// All Parcelables MUST have a CREATOR
	  public static final Parcelable.Creator<DualModel> CREATOR = new Parcelable.Creator<DualModel>() 
	  {
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
