package edu.psu.cmpsc483w.moviesearch2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageDiskCache {

	private File cacheDir;
	private int maxSize;

	// The default max size in bytes is 5MB
	private final static int DEFAULT_MAX_SIZE = 5242880;

	public ImageDiskCache(Context context) {
		this(context, DEFAULT_MAX_SIZE);
	}

	public ImageDiskCache(Context context, int maxSize) {
		// If external storage available (that isn't removable or mounted), use
		// that
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {

			this.cacheDir = context.getExternalCacheDir();

			// On emulator, context.getExternalCacheDir() was returning null
			if (this.cacheDir == null) {
				this.cacheDir = context.getCacheDir();
			}
		}
		// Otherwise, use the cache which is more prone to the OS's garbage
		// collection
		else {
			this.cacheDir = context.getCacheDir();
		}

		this.maxSize = maxSize;
	}

	public Bitmap getBitmap(String url) {
		String filePath = this.getFilePath(url);

		File file = new File(filePath);

		if (file.exists()) {
			return BitmapFactory.decodeFile(filePath);
		}
		return null;

	}

	public void putBitmap(String url, Bitmap bitmap) {
		String filePath = this.getFilePath(url);

		File file = new File(filePath);

		if (file.exists()) {
			file.delete();
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// File path is hash code to prevent illegal characters in the url from
	// messing up the path
	private String getFilePath(String url) {
		return this.cacheDir.getAbsolutePath() + File.separator
				+ url.hashCode();
	}

	public void trimCache() {
		File[] files = this.cacheDir.listFiles();

		// Size of the current cache
		long size = this.getCurrentSize();
		// The number of bytes to trim
		long bytes = size - this.maxSize;

		for (File file : files) {
			if (bytes <= 0) {
				return;
			}

			bytes -= file.length();
			file.delete();
		}
	}

	private long getCurrentSize() {
		File[] files = this.cacheDir.listFiles();

		long size = 0;

		for (File file : files) {
			size += file.length();
		}

		return size;
	}

	public void clear() {
		File[] files = this.cacheDir.listFiles();

		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}

}
