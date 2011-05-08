package org.dbartists;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

public class MusicDownloader {
	
	private final static String TAG = "MusicDownloader";


	class FilesLoader extends Thread {
		
		private void download(String url, String path) {

			InputStream input;
			try {
				input = new BufferedInputStream(
						new URL(url).openStream());
				OutputStream output = new FileOutputStream(path);

				byte data[] = new byte[1024];

				int count = 0;

				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (MalformedURLException e) {
				Log.e(TAG, "Download Error: ", e);
			} catch (IOException e) {
				Log.e(TAG, "Download Error: ", e);
			}

		}
		
		@Override
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (downloadQueue.fileToLoad.size() == 0)
						synchronized (downloadQueue.fileToLoad) {
							downloadQueue.fileToLoad.wait();
						}
					if (downloadQueue.fileToLoad.size() != 0) {
						FileToLoad fileToLoad;
						synchronized (downloadQueue.fileToLoad) {
							fileToLoad = downloadQueue.fileToLoad.pop();
						}
						download(fileToLoad.url, fileToLoad.path);
						cache.put(fileToLoad.url, fileToLoad.path);
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	// stores list of photos to download
	class PhotosQueue {
		private Stack<FileToLoad> fileToLoad = new Stack<FileToLoad>();

		// removes all instances of this ImageView
		public void clean(String url) {
			for (int j = 0; j < fileToLoad.size();) {
				if (fileToLoad.get(j).url.equals(url))
					fileToLoad.remove(j);
				else
					++j;
			}
		}
	}

	// Task for the queue
	private class FileToLoad {
		public String url;
		public String path;
		
		public FileToLoad(String u, String p) {
			url = u;
			path = p;
		}
	}

	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	private HashMap<String, String> cache = new HashMap<String, String>();

	private File cacheDir;

	PhotosQueue downloadQueue = new PhotosQueue();

	FilesLoader photoLoaderThread = new FilesLoader();

	public MusicDownloader(Context context) {
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"dbartists");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public void clearCache() {
		// clear memory cache
		cache.clear();

		// clear SD cache
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

	public void download(String url, String path) {
		if (!cache.containsKey(url)) {
			queueDownload(url, path);
		}
	}


	private void queueDownload(String url, String path) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		downloadQueue.clean(url);
		FileToLoad p = new FileToLoad(url, path);
		synchronized (downloadQueue.fileToLoad) {
			downloadQueue.fileToLoad.push(p);
			downloadQueue.fileToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	public void stopThread() {
		photoLoaderThread.interrupt();
	}

}
