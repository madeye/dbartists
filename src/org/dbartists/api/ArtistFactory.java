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

public class ArtistFactory {

	public static final String TAG = "ArtistFactory";

	public static List<Artist> downloadArtists(String url, int startId) {
		Log.d(TAG, "url: " + url);
		ArrayList<Artist> list = new ArrayList<Artist>();
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
			if (!line.startsWith("#genre"))
				return null;
			reader.readLine();
			line = reader.readLine();
			if (!line.startsWith("#artists"))
				return null;

			// fetch artists
			int n = 0;
			while (true) {

				// artist name
				line = reader.readLine();
				if (line == null)
					break;
				String name = line.trim();

				// artist image
				line = reader.readLine();
				String img = line.trim();

				// artist image
				line = reader.readLine();
				String site = line.trim();

				Artist art = new Artist(name, img, site);
				list.add(art);
				n++;
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "url error:", e);
		} catch (IOException e) {
			Log.e(TAG, "fetch error", e);
		}
		return list;
	}

}
