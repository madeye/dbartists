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

public class ArtistInfoFactory {

	public static final String TAG = "ArtistFactory";

	public static ArtistInfo downloadArtist(String url) {
		Log.d(TAG, "url: " + url);
		ArtistInfo artistInfo = new ArtistInfo();
		try {
			URL aURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) aURL.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(5000);
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = reader.readLine();
			if (line == null)
				return null;

			if (!line.startsWith("#artist"))
				return null;
			artistInfo.setName(reader.readLine());

			line = reader.readLine();
			if (!line.startsWith("#genre"))
				return null;
			artistInfo.setGenre(reader.readLine());

			line = reader.readLine();
			if (!line.startsWith("#member"))
				return null;
			artistInfo.setMember(reader.readLine());

			line = reader.readLine();
			if (!line.startsWith("#company"))
				return null;
			artistInfo.setCompany(reader.readLine());

		} catch (MalformedURLException e) {
			Log.e(TAG, "url error:", e);
		} catch (IOException e) {
			Log.e(TAG, "fetch error", e);
		}
		return artistInfo;
	}

}
