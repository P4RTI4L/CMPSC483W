package edu.psu.cmpsc483w.moviesearch;

import android.os.Parcel;
import android.os.Parcelable;

// A data object containing a reduced set of fields of the result of a movie search/actor listing

public class MovieListingData implements Parcelable {
	
	// Whether the movie is an adult movie
	private boolean adult;
	// The title of the movie
	private String title;
	// The id of the movie in TMDB
	private int id;
	// The release date of the movie
	private String releaseDate;
	// The poster path of the movie relative to TMDB
	private String poster;
	
	// Constructor
	public MovieListingData(boolean adult, String title, int id, String releaseDate, String poster)
	{
		this.adult = adult;
		this.title = title;
		this.id = id;
		if (releaseDate == null || releaseDate.equals("null"))
		{
			this.releaseDate = "Release Date Unavailable";
		}
		else
		{
			this.releaseDate = releaseDate;
		}
		this.poster = poster;
	}
	
	// Constructor from Parcel
	public MovieListingData(Parcel in)
	{
		// Read the values from the parcel in the order that they were written
		
		// Parcel can't store booleans so byte was used instead
		this.adult = (in.readByte() == 1);
		// Read from the parcel as usual
		this.title = in.readString();
		this.id = in.readInt();
		this.releaseDate = in.readString();
		this.poster = in.readString();
	}
	
	// Getter methods
	
	public boolean isAdult()
	{
		return adult;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getReleaseDate()
	{
		return releaseDate;
	}
	
	public String getPosterPath()
	{
		return poster;
	}

	/* Functions needed for serializing/parceling the object */
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// Write the object data to a parcel to flatten the object
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// Parcel doesn't have write boolean so write a 1 if true and a 0 if false
		out.writeByte((byte)(adult ? 1 : 0));
		// Write the rest of the variables as usual
		out.writeString(title);
		out.writeInt(id);
		out.writeString(releaseDate);
		out.writeString(poster);
	}
	
	@Override
	// Overrides the equals method which simply checks if the two id's are equal
	public boolean equals(Object data)
	{
	  return this.id == ((MovieListingData)data).id;
	}
	  
	@Override
	  public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + new Integer(id).hashCode();
	      return result;
	  }
	
	
	/* Functions needed for de-serializing/de-parceling back to the object */
	
	// All Parcelables MUST have a CREATOR
	public static final Parcelable.Creator<MovieListingData> CREATOR = new Parcelable.Creator<MovieListingData>() 
	{
		// Create a MovieListingData object from a Parcel
		@Override
		public MovieListingData createFromParcel(Parcel source) {
			return new MovieListingData(source);
		}

		@Override
		public MovieListingData[] newArray(int size) {
			return new MovieListingData[size];
		}
		
	};
}
