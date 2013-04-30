package edu.psu.cmpsc483w.moviesearch2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class ActorSubsearchModel extends PagedModel {

	private String name;
	
	public ActorSubsearchModel(String name)
	{
		super();
		
		this.name = name;
	}
	
	public ActorSubsearchModel(Parcel in) {
		
		super(in);

		this.name = in.readString();
	}
	

	// Changes the name and resets the data
	public void changeActorName(String name)
	{
		this.name = name;
		resetData();
	}
	
	// Overloaded version of synchronousActorSearch that allows one to specify the page of results
	private static Pair<Object[],Integer> synchronousActorSubsearch(String nameSubstring, int page)
	{
		JSONObject json = TmdbModel.executeQuery("search/person", new String[]{"query", "page"},
				new String[]{nameSubstring,Integer.toString(page)});
		
		try {
			JSONArray resultsArray = json.getJSONArray("results");
			
			Integer numPages = json.getInt("total_pages");
			ActorData[] actors = new ActorData[resultsArray.length()];
			
			for (int i=0; i<resultsArray.length(); i++)
			{
				JSONObject entry = resultsArray.getJSONObject(i);
				actors[i] = new ActorData(
						entry.getBoolean("adult"),
						entry.getString("name"),
						entry.getInt("id"),
						entry.getDouble("popularity"),
						entry.getString("profile_path"));
			}
			
			return new Pair<Object[],Integer>(actors, numPages);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected Pair<Object[], Integer> fetchNewResults() {
		return synchronousActorSubsearch(name, nextPage);
	}

	@Override
	public long getDataId(int position) {
		ActorData data = (ActorData)getData(position);
		
		if (data != null) {
			return data.getId();
		}
		// Default case
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		
		dest.writeString(this.name);
	}
	
	public final static Parcelable.Creator<ActorSubsearchModel> CREATOR = new Parcelable.Creator<ActorSubsearchModel>() {

		@Override
		public ActorSubsearchModel createFromParcel(Parcel source) {
			return new ActorSubsearchModel(source);
		}

		@Override
		public ActorSubsearchModel[] newArray(int size) {
			return new ActorSubsearchModel[size];
		}
		
	};
}
