package edu.psu.cmpsc483w.moviesearch2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageModel {
	
	private LruCache<String,Bitmap> memCache;
	private ImageDiskCache diskCache;
	private Bitmap placeholder;
	private Map<ImageView, BitmapTask> lastTasks;
	
	public ImageModel(Context context, int placeholderId)
	{
		int maxMemory = (int)(Runtime.getRuntime().maxMemory());
		
		memCache = new LruCache<String, Bitmap>(maxMemory/8) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap)
			{
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
		diskCache = new ImageDiskCache(context);
		
		placeholder = BitmapFactory.decodeResource(context.getResources(), placeholderId);
		
		lastTasks = Collections.synchronizedMap(new HashMap<ImageView,BitmapTask>());
	}
	
	public void setImageViewWithUrl(ImageView image, String url, String type)
	{
		if (url.equals(""))
		{
			image.setImageBitmap(placeholder);
			return;
		}
		
		Bitmap bitmap = memCache.get(url);
		
		if (bitmap != null)
		{
			image.setImageBitmap(bitmap);
		}
		else
		{
			image.setImageBitmap(placeholder);
			
			BitmapTask newTask = new BitmapTask(image, url, type);
			
			BitmapTask oldTask = lastTasks.get(image);
			
			// Fixes the reuse problem with asynchronous fetching, cancel any old tasks that are trying to use that same
			// view and run the new task instead
			if (oldTask != null && !oldTask.url.equals(newTask.url))
			{
				oldTask.cancel(true);
			}
			lastTasks.put(image, newTask);
			newTask.execute();
			
		}
	}
		
	// Synchronously downloads the image located at the given url
	private static Bitmap synchronousImageDownload(String url)
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
	private static Bitmap synchronousTmdbRelativeImageDownload(String relativeUrl, String type, int width)
	{
		return synchronousImageDownload(TmdbModel.getImageUrl(type, relativeUrl, width));
	}
	
	private class BitmapTask extends AsyncTask<Void, Void, Bitmap> {

		private ImageView image;
		private String url;
		private String type;
		
		public BitmapTask(ImageView image, String url, String type)
		{
			this.image = image;
			this.url = url;
			this.type = type;
		}
		

		@Override
		protected Bitmap doInBackground(Void... params) {
			// Check the disk cache first
			Bitmap bitmap = diskCache.getBitmap(url);
			
			if (bitmap != null)
				return bitmap;
			
			// Get the full url and download it
			String fullUrl = TmdbModel.getImageUrl(type, url, image.getWidth());
			
			return synchronousImageDownload(fullUrl);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			// Possible that the activity was destroyed since this was created, if so
			// can't trust any references
			if (image != null && bitmap != null)
			{
				// If it still exists, set the imageview and add it to the caches
				image.setImageBitmap(bitmap);
				
				diskCache.putBitmap(url, bitmap);
				memCache.put(url, bitmap);
			}
		}
		
	}
}
