package edu.psu.cmpsc483w.moviesearch2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageDiskCache {

	private File cacheDir;
	
	public ImageDiskCache(Context context)
	{
		// If external storage available (that isn't removable or mounted), use that
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable())
		{
			cacheDir = context.getExternalCacheDir();
			
			// On emulator, context.getExternalCacheDir() was returning null
			if (cacheDir == null)
			{
				cacheDir = context.getCacheDir();
			}
		}
		// Otherwise, use the cache which is more prone to the OS's garbage collection
		else
		{
			cacheDir = context.getCacheDir();
		}
	}

	
	public Bitmap getBitmap(String url)
	{
		String filePath = getFilePath(url);
		
		File file = new File(filePath);
		
		if (file.exists())
			return BitmapFactory.decodeFile(filePath);
		return null;
		
	}
	
	public void putBitmap(String url, Bitmap bitmap)
	{
		String filePath = getFilePath(url);
		
		File file = new File(filePath);
		
		if (file.exists())
			file.delete();
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// File path is hash code to prevent illegal characters in the url from messing up the path
	private String getFilePath(String url)
	{
		return cacheDir.getAbsolutePath() + File.separator + url.hashCode();
	}
	
	public void clear()
	{
		File[] files = cacheDir.listFiles();
		
		if (files != null)
		{
			for (File file: files)
			{
				file.delete();
			}
		}
	}
	
}
