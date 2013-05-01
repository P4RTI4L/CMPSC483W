package edu.psu.cmpsc483w.moviesearch2;

public interface CachedDataSource {

	// Returns whether there has been data cached at the given position
	public boolean isDataCached(int position);

	// Returns the total number of data objects (not necessarily cached)
	public int getDataCount();

	// Returns the total number of data objects that have been cached
	public int getCachedDataCount();

	// Returns the object at the given position, may not necessarily be cached
	// so may be a delay
	public Object getData(int position);

	// Returns an id for the object at the given position, may not necessarily
	// be cached
	public long getDataId(int position);

}
