package edu.psu.cmpsc483w.moviesearch2;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailModel {
	  
	// Synchronously queries the database given an id of a movie for the basic information of a movie
	public static DetailData synchronousDetailPrimaryRetrieve(int movieId) {
		
	    JSONObject result = TmdbModel.executeQuery("movie/"+movieId, null, null);
	    
	    if (result != null)
	    {
	      try {
	    	  JSONArray genresList = result.getJSONArray("genres");
	    	  ArrayList<String> genres = new ArrayList<String>();
	      
	    	  for (int i=0; i<genresList.length(); i++) {
	    		  genres.add(genresList.getJSONObject(i).getString("name"));
	    	  }
      
	    	  return new DetailData(
	    			  result.getBoolean("adult"),
	    			  genres,
	    			  result.getInt("id"),
	    			  result.getString("overview"),
	    			  result.getDouble("popularity"),
	    			  result.getString("poster_path"),
	    			  result.getString("release_date"),
	    			  result.getInt("runtime"),
	    			  result.getString("title"),
	    			  result.getString("tagline"),
	    			  result.getDouble("vote_average"));
	      } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }
	    
	    return null;
	}
    
	  // Synchronously queries the database given an id of a movie for a list of the names of the actors
	  public static ArrayList<String> synchronousDetailCastRetrieve(int movieId) {
		  
		  JSONObject query = TmdbModel.executeQuery("movie/"+movieId+"/casts", null, null); 
			  
		  if (query != null) {
			  ArrayList<String> result = new ArrayList<String>();
		  
			  try {
				  JSONArray cast = query.getJSONArray("cast");
		    
				  for (int i=0; i<cast.length(); i++) {
					  result.add(cast.getJSONObject(i).getString("name"));
				  }
			  } catch (JSONException e) {
				  // TODO Auto-generated catch block
					  e.printStackTrace();
		      	  }
		      
				  return result;
			  }
		    
			  return null;
		  }
}
