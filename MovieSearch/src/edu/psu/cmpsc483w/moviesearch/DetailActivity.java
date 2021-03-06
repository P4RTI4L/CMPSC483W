package edu.psu.cmpsc483w.moviesearch;

import java.util.ArrayList;

import edu.psu.cmpsc483w.moviesearch.R;
import edu.psu.cmpsc483w.moviesearch.R.layout;
import edu.psu.cmpsc483w.moviesearch.R.menu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private DetailData detailInfo;
	private ArrayList<String> castNames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		    
		if (savedInstanceState != null)
		{
			detailInfo = savedInstanceState.getParcelable("detailInfo");
			setUpDetailInfo(detailInfo);
			castNames = savedInstanceState.getStringArrayList("castNames");
			setUpCastView(castNames);
		}
		else
		{
			Intent intent = getIntent();
			new AsyncTaskDetailQuery().execute(intent.getIntExtra("movieId", 0), DetailModel.DETAIL_PRIMARY);
			new AsyncTaskDetailQuery().execute(intent.getIntExtra("movieId", 0), DetailModel.DETAIL_CAST);
		}    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}
  
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {

    savedInstanceState.putParcelable("detailInfo", detailInfo);
    savedInstanceState.putStringArrayList("castNames", castNames);
      
      // Always call the superclass so it can save the view hierarchy state
      super.onSaveInstanceState(savedInstanceState);
  }
  
  // Sets up the layout elements using the info stored in detailInfo
  public void setUpDetailInfo(DetailData detailInfo)
  {
         // Set the values of TextViews to their respective value
    ((TextView)findViewById(R.id.detail_movie_title)).setText(detailInfo.getTitle());
    ((TextView)findViewById(R.id.detail_release_date)).setText(detailInfo.getReleaseDate());
    ((TextView)findViewById(R.id.detail_rating)).setText(""+detailInfo.getVoteAverage());
    ((TextView)findViewById(R.id.detail_tagline)).setText(detailInfo.getTagline());
    ((TextView)findViewById(R.id.detail_running_time)).setText(""+detailInfo.getRunTime());

    // Need to convert the genres list to a comma separated string
    String genresList = detailInfo.getGenres().toString();
    genresList = genresList.substring(1, genresList.length()-1).replace(",", ", ");
    
    ((TextView)findViewById(R.id.detail_genre_list)).setText(genresList);
    ((TextView)findViewById(R.id.detail_overview_content)).setText(detailInfo.getOverview());
  }
  
  // Sets up the listView of cast names using the names stored in castNames
  public void setUpCastView(ArrayList<String> castNames)
  {
    TextView castList = (TextView)findViewById(R.id.detail_cast_list);
    String castString = castNames.toString();
    castString = castString.substring(1, castString.length()-1).replace(",","\n");
    castList.setText(castString);
  }
  
  // Web queries should always be performed asynchronously to prevent blocking the UI thread, for this purpose
  //  an AsyncTask is needed to update the interface as data is ready
  private class AsyncTaskDetailQuery extends AsyncTask<Integer, Void, Pair<Integer,Object>>
  {
    @Override
    protected Pair<Integer,Object> doInBackground(Integer... params) {

      Integer movieId = params[0];
      Integer type = params[1];    
      
      if (type == DetailModel.DETAIL_PRIMARY)
      {
        return new Pair<Integer,Object>(type,DetailModel.synchronousDetailPrimaryRetrieve(movieId));
      }
      else if (type == DetailModel.DETAIL_CAST)
      {
        return new Pair<Integer,Object>(type,DetailModel.synchronousDetailCastRetrieve(movieId));
      }
          
      return null;
    }
    
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Pair<Integer,Object> result)
    {
      if (result != null)
      {
        if (result.first == DetailModel.DETAIL_PRIMARY)
        {
          detailInfo = (DetailData)result.second;
          setUpDetailInfo(detailInfo);
        }
        else
        {
          castNames = new ArrayList<String>((ArrayList<String>)result.second);
          setUpCastView(castNames);
        }
      }
    }
        
  }
}
