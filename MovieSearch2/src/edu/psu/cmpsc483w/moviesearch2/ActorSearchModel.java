package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class ActorSearchModel implements CachedDataSource, Parcelable {
	
	// The statuses that a query can have
	private final static int STATUS_UNINITIALIZED = 0;
	private final static int STATUS_NOT_CACHED = 1;
	private final static int STATUS_CACHED = 2;
	
	// The actor id being queried, the status of the query, and the result of the query
	// The whole point is to store intermediate values to avoid making queries
	private int actorQuery;
	private int queryStatus;
	private ArrayList<MovieListingData> queryResults;
	private ArrayList<Integer> excludeActors;
	private ArrayList<Integer> excludeStatus;
	private ArrayList<MovieListingData[]> excludeResults;
	
	// Whether all the queries have been cached, as there can be potentially many requests at once,
	// it's important to store whether everything is ready rather than repeatedly check
	private boolean allQueriesCached;
	
	// The final result to return for each request
	private ArrayList<MovieListingData> results;
	
	public ActorSearchModel()
	{
		allQueriesCached = true;
		
		queryStatus = STATUS_UNINITIALIZED;
		queryResults = new ArrayList<MovieListingData>();
		excludeActors = new ArrayList<Integer>();
		excludeStatus = new ArrayList<Integer>();
		excludeResults = new ArrayList<MovieListingData[]>();
		
		results = new ArrayList<MovieListingData>();
	}
	
	public ActorSearchModel(int actorQuery)
	{
		this();
		this.actorQuery = actorQuery;
		this.queryStatus = STATUS_NOT_CACHED;
		
		allQueriesCached = false;
	}
	
	public ActorSearchModel(Parcel in)
	{
		this.actorQuery = in.readInt();
		this.queryStatus = in.readInt();
		
		this.queryResults = new ArrayList<MovieListingData>();
		in.readList(this.queryResults, null);
		
		this.excludeActors = new ArrayList<Integer>();
		this.excludeStatus = new ArrayList<Integer>();
		this.excludeResults = new ArrayList<MovieListingData[]>();
		
		in.readList(this.excludeActors, null);
		in.readList(this.excludeStatus, null);
		in.readList(this.excludeResults, null);
		
		this.allQueriesCached = (in.readByte() == 1);
		
		this.results = new ArrayList<MovieListingData>();
		in.readList(this.results, null);
	}
	
	// Clears all data from the model
	public void clearModel()
	{
		this.actorQuery = 0;
		this.queryStatus = STATUS_UNINITIALIZED;
		this.queryResults.clear();
		
		this.excludeActors.clear();
		this.excludeResults.clear();
		this.excludeStatus.clear();
		
		allQueriesCached = true;
	}
	
	
	
	// Sets the query actor, clearing any related previous data, does nothing if that actor is
	// already in the exclude list (or the same actor), returns true if successful, false otherwise
	public boolean setQueryActor(int actorQueryId)
	{
		if (actorQueryId != this.actorQuery && !isActorExcluded(actorQueryId))
		{
			queryStatus = STATUS_NOT_CACHED;
			this.actorQuery = actorQueryId;
			this.queryResults.clear();
			
			allQueriesCached = false;
			
			return true;
		}
		
		return false;
	}
	
	// Adds an actor to exclude, returns true if successful, false otherwise
	public boolean addExcludeActor(int actorExcludeId)
	{
		if (isActorExcluded(actorExcludeId) && actorExcludeId != actorQuery)
		{
			excludeActors.add(actorExcludeId);
			excludeResults.add(new MovieListingData[]{});
			excludeStatus.add(STATUS_NOT_CACHED);
			
			allQueriesCached = false;
			
			return true;
		}
		
		return false;
	}
	
	public boolean isActorExcluded(int actorExcludeId)
	{
		for (int i=0; i<excludeActors.size(); i++)
		{
			if (excludeActors.get(i) == actorExcludeId)
				return true;
		}
		
		return false;
	}
	
	public void removeExcludeActor(int actorExcludeId)
	{
		for (int i=0; i<excludeActors.size(); i++)
		{
			if (excludeActors.get(i) == actorExcludeId)
			{
				excludeActors.remove(i);
				excludeResults.remove(i);
				excludeStatus.remove(i);
				
				break;
			}
		}
		
		calculateResults();
	}
	
	// Perform any queries that need to be cached yet
	public void performQueries()
	{
		// Whether the results need to be updated
		boolean needsUpdate = false;
		
		if (queryStatus != STATUS_UNINITIALIZED && queryStatus != STATUS_CACHED)
		{
			MovieListingData[] results = synchronousCastSearch(actorQuery);
			queryResults.addAll(Arrays.asList(results));
			queryStatus = STATUS_CACHED;
			needsUpdate = true;
		}
		
		for (int i=0; i<excludeActors.size(); i++)
		{
			if (excludeStatus.get(i) == STATUS_NOT_CACHED)
			{
				MovieListingData[] results = synchronousCastSearch(excludeActors.get(i));
				excludeResults.set(i, results);
				excludeStatus.set(i, STATUS_CACHED);
				needsUpdate = true;
			}
		}
		
		if (needsUpdate)
		{
			allQueriesCached = true;
			calculateResults();
		}
	}
	
	private void calculateResults()
	{
		results.clear();
		results.addAll(queryResults);
		
		for (int i=0; i<excludeResults.size(); i++)
		{
			results.removeAll(Arrays.asList(excludeResults.get(i)));
		}
	}
	
	
	// Synchronously queries the database for the movies of an actor
	public static MovieListingData[] synchronousCastSearch(int actorIdQuery) {
		JSONObject json = TmdbModel.executeQuery("person/"+actorIdQuery+"/credits", null, null);
		
		try {
			JSONArray castArray = json.getJSONArray("cast");
			MovieListingData results[] = new MovieListingData[castArray.length()];
			
			for (int i=0; i<castArray.length(); i++)
			{
				JSONObject entry = castArray.getJSONObject(i);
				
				results[i] = new MovieListingData(
						entry.getBoolean("adult"),
						entry.getString("title"),
						entry.getInt("id"),
						entry.getString("release_date"),
						entry.getString("poster_path"));
			}
			
			return results;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	// Whether data is cached at a point is determined by if all the queries are complete
	@Override
	public boolean isDataCached(int position) {

		return allQueriesCached;
	}

	// Data size is unknown before all results are cached, so if not cached return 1, otherwise return the size
	@Override
	public int getDataCount() {
		// TODO Auto-generated method stub
		if (allQueriesCached)
		{
			return results.size();
		}
		else
		{
			return 1;
		}
	}

	@Override
	public int getCachedDataCount() {
		// TODO Auto-generated method stub
		return results.size();
	}

	// Return the result if ready, otherwise perform the queries and then return
	@Override
	public Object getData(int position) {
		// TODO Auto-generated method stub
		if (!allQueriesCached)
		{
			performQueries();
		}
		
		return results.get(position);

	}

	@Override
	public long getDataId(int position) {
		// TODO Auto-generated method stub
		MovieListingData movie = (MovieListingData)getData(position);
		
		return movie.getId();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(actorQuery);
		dest.writeInt(queryStatus);
		dest.writeList(queryResults);
		
		dest.writeList(excludeActors);
		dest.writeList(excludeStatus);
		dest.writeList(excludeResults);
		
		dest.writeByte((byte)(allQueriesCached ? 1 : 0));
		dest.writeList(results);
	}
	
  // All Parcelables MUST have a CREATOR
  public static final Parcelable.Creator<ActorSearchModel> CREATOR = new Parcelable.Creator<ActorSearchModel>() 
  {
    // Create a DetailData object from a Parcel
    @Override
    public ActorSearchModel createFromParcel(Parcel source) {
      return new ActorSearchModel(source);
    }

    @Override
    public ActorSearchModel[] newArray(int size) {
      return new ActorSearchModel[size];
    }
      
  };
  

  
  
}
