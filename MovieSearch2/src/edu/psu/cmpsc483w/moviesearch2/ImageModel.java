package edu.psu.cmpsc483w.moviesearch2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageModel {

	// Synchronously downloads the image located at the given url
	public static Bitmap synchronousImageDownload(String url)
	{
		Bitmap bitmap = null;
		InputStream in;
		
	    try {
	        in = new URL(url).openStream();
	        bitmap = BitmapFactory.decodeStream(in);
	        in.close();
	    } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	// Synchronously downloads the iamge located at the url relative to the tmdb api
	public static Bitmap synchronousTmdbRelativeImageDownload(String relativeUrl, String type, int width)
	{
		return synchronousImageDownload(TmdbModel.getImageUrl(type, relativeUrl, width));
	}
}
