package edu.psu.cmpsc483w.moviesearch2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class ActorSearchModel {

	// Synchronously query the database given a substring of an actor name
	public static Pair<ActorData[],Integer> synchronousActorSearch(String nameSubstring)
	{
		return synchronousActorSearch(nameSubstring, 1);
	}
	
	// Overloaded version of synchronousActorSearch that allows one to specify the page of results
	public static Pair<ActorData[],Integer> synchronousActorSearch(String nameSubstring, int page)
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
			
			return new Pair<ActorData[],Integer>(actors, numPages);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
