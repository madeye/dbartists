package org.dbartists.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TrackFactory {

	public static final String TAG = "TrackFactory";

	public static List<Track> downloadTracks(String url, int startId) {
		Log.d(TAG, "url: " + url);
		ArrayList<Track> list = new ArrayList<Track>();
		try {
			URL aURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = reader.readLine();
			if (line == null)
				return null;
			if (!line.startsWith("#artist"))
				return null;
			reader.readLine();
			line = reader.readLine();
			if (!line.startsWith("#titles"))
				return null;
			
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> urls = new ArrayList<String>();
			
			// fetch titles
			while (true) {
				// title
				line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith("#urls"))
					break;
				titles.add(line.trim());
			}
			
			// fetch urls
			while (true) {
				// title
				line = reader.readLine();
				if (line == null)
					break;
				urls.add(line.trim());
			}
			
			for (int n = 0; n < titles.size(); n++) {
				Log.d(TAG, "track: " + titles.get(n) + " url: " + urls.get(n));
				list.add(new Track(startId + n, titles.get(n), urls.get(n)));
			}
				
				
		} catch (MalformedURLException e) {
			Log.e(TAG, "url error:", e);
		} catch (IOException e) {
			Log.e(TAG, "fetch error", e);
		} catch (IndexOutOfBoundsException e) {
			Log.e(TAG, "decode error", e);
		}
		return list;
	}

}
