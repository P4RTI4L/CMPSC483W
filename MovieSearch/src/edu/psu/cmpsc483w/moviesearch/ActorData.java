package edu.psu.cmpsc483w.moviesearch;

import android.os.Parcel;
import android.os.Parcelable;

// A data object containing the fields returned by the TMDB Api for an Actor search

public class ActorData implements Parcelable {

	// Whether this actor performs in adult movies
	private boolean adult;
	// The name of the actor
	private String name;
	// The id of the actor in TMDB
	private int id;
	// The popularity of the actor as defined by TMDB
	private double popularity;
	// The profile path of the actor relative to TMDB
	private String profile;

	// Constructor
	public ActorData(boolean adult, String name, int id, double popularity,
			String profile) {
		this.adult = adult;
		this.name = name;
		this.id = id;
		this.popularity = popularity;
		this.profile = profile;
	}

	// Constructor from Parcel
	public ActorData(Parcel in) {
		// Read the values from the parcel in the order that they were written

		// Parcel can't store booleans so byte was used instead
		this.adult = (in.readByte() == 1);
		// Read from the parcel as usual
		this.name = in.readString();
		this.id = in.readInt();
		this.popularity = in.readDouble();
		this.profile = in.readString();
	}

	// Getter methods

	public boolean isAdult() {
		return adult;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public double getPopularity() {
		return popularity;
	}

	public String getProfilePath() {
		return profile;
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
		// Parcel doesn't have write boolean so write a 1 if true and a 0 if
		// false
		out.writeByte((byte) (adult ? 1 : 0));
		// Write the rest of the variables as usual
		out.writeString(name);
		out.writeInt(id);
		out.writeDouble(popularity);
		out.writeString(profile);
	}

	/* Functions needed for de-serializing/de-parceling back to the object */

	// All Parcelables MUST have a CREATOR
	public static final Parcelable.Creator<ActorData> CREATOR = new Parcelable.Creator<ActorData>() {
		// Create an ActorData object from a Parcel
		@Override
		public ActorData createFromParcel(Parcel source) {
			return new ActorData(source);
		}

		@Override
		public ActorData[] newArray(int size) {
			return new ActorData[size];
		}

	};
}
