package edu.psu.cmpsc483w.moviesearch;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailData implements Parcelable {

  // Whether the movie is an adult movie
  private boolean adult;
  // The genres of the movie
  private ArrayList<String> genres;
  // The id of the movie
  private int id;
  // The overview of the movie
  private String overview;
  // The popularity of the movie according to TMDB
  private double popularity;
  // The poster path of the movie
  private String posterPath;
  // The date the movie was released
  private String releaseDate;
  // The runtime of the movie in minutes
  private int runTime;
  // The title of the movie
  private String title;
  // The tagline of the movie
  private String tagline;
  // The average vote rating for the movie
  private double voteAverage;
  
  // Constructors
  public DetailData(boolean adult, ArrayList<String> genres, int id, String overview, double popularity, String posterPath, String releaseDate, int runTime, String title, String tagline, double voteAverage)
  {
    this.adult = adult;
    this.genres = new ArrayList<String>(genres);
    this.id = id;
    this.overview = overview;
    this.popularity = popularity;
    this.posterPath = posterPath;
    this.releaseDate = releaseDate;
    this.runTime = runTime;
    this.title = title;
    this.tagline = tagline;
    this.voteAverage = voteAverage;
  }
  
  public DetailData(Parcel in)
  {
    // Read from the parcel in the order that items were written to it
    this.adult = (in.readByte() == 1);
    this.genres = new ArrayList<String>();
    in.readList(this.genres, null);
    this.id = in.readInt();
    this.overview = in.readString();
    this.popularity = in.readDouble();
    this.posterPath = in.readString();
    this.releaseDate = in.readString();
    this.runTime = in.readInt();
    this.title = in.readString();
    this.tagline = in.readString();
    this.voteAverage = in.readDouble();
  }
  
  // Accesssors
  
  public boolean getAdult()
  {
    return adult;
  }
  
  public ArrayList<String> getGenres()
  {
    return genres;
  }
  
  public int getId()
  {
    return id;
  }
  
  public String getOverview()
  {
    return overview;
  }
  
  public double getPopularity()
  {
    return popularity;
  }
  
  public String getPosterPath()
  {
    return posterPath;
  }
  
  public String getReleaseDate()
  {
    return releaseDate;
  }
  
  public int getRunTime()
  {
    return runTime;
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public String getTagline()
  {
    return tagline;
  }
  
  public double getVoteAverage()
  {
    return voteAverage;
  }
  
  
  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }
  // Write the object to a parcel to flatten the data
  @Override
  public void writeToParcel(Parcel out, int flags) {
    // Parcel doesn't have a writeBoolean method so write a byte with a 1 or 0 instead
    out.writeByte((byte)(adult ? 1 : 0));
    // Write the rest of the values as normal
    out.writeList(genres);
    out.writeInt(id);
    out.writeString(overview);
    out.writeDouble(popularity);
    out.writeString(posterPath);
    out.writeString(releaseDate);
    out.writeInt(runTime);
    out.writeString(title);
    out.writeString(tagline);
    out.writeDouble(voteAverage);
  }
  
  // All Parcelables MUST have a CREATOR
  public static final Parcelable.Creator<DetailData> CREATOR = new Parcelable.Creator<DetailData>() 
  {
    // Create a DetailData object from a Parcel
    @Override
    public DetailData createFromParcel(Parcel source) {
      return new DetailData(source);
    }

    @Override
    public DetailData[] newArray(int size) {
      return new DetailData[size];
    }
      
  };
}