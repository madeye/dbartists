// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.dbartists;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbartists.api.Artist;
import org.dbartists.api.ArtistFactory;
import org.dbartists.utils.Tracker;

public class ArtistsListActivity extends PlayerActivity implements
		OnItemClickListener {

	private final static String TAG = "ArtistsListActivity";
	private String apiUrl = Constants.GENRE_ARTIST_API_URL;
	private int genreId = 1;
	private int page = 1;
	private String description;

	protected ArtistsListAdapter listAdapter;

	private static Map<String, Artist> artistsCache = new HashMap<String, Artist>();

	public static Artist getArtistFromCache(String artistId) {
		Artist result = artistsCache.get(artistId);
		if (result == null) {
			// result = ArtistFactory.downloadArtist(artistId);
			// artistsCache.put(artistId, result);
		}
		return result;
	}

	public static void addAllToArtistCache(List<Artist> artists) {
		for (Artist artist : artists) {
			artistsCache.put(artist.getName(), artist);
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		artistsCache.clear();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		genreId = getIntent().getIntExtra(Constants.EXTRA_GENRE_ID, 1);
		page = getIntent().getIntExtra(Constants.EXTRA_PAGE, 1);

		Resources res = getResources();
		String[] genre_entries = res.getStringArray(R.array.genre_entry);
		description = genre_entries[genreId - 1];
		
		super.onCreate(savedInstanceState);
		ViewGroup container = (ViewGroup) findViewById(R.id.Content);
		ViewGroup.inflate(this, R.layout.news, container);

		ListView listView = (ListView) findViewById(R.id.ListView01);
		listView.setOnItemClickListener(this);
		listAdapter = new ArtistsListAdapter(ArtistsListActivity.this);
		listView.setAdapter(listAdapter);

		addArtists();
		

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Artist s = (Artist) parent.getAdapter().getItem(position);
		if (s == null) {
			addArtists();
		} else {
			// Intent i = new Intent(this, NewsArtistActivity.class);
			// i.putExtra(Constants.EXTRA_Artist_ID, s.getId());
			// i.putExtra(Constants.EXTRA_DESCRIPTION, description);
			// startActivityWithoutAnimation(i);
		}
	}

	private void addArtists() {

		apiUrl = apiUrl + "?g=" + genreId + "&p=" + page;

		listAdapter.addMoreArtists(apiUrl, 20 * (page - 1));

		page++;
	}

	@Override
	public CharSequence getMainTitle() {
		Log.d(TAG, description);
		return description;
	}

	@Override
	public void trackNow() {
		StringBuilder pageName = new StringBuilder("News")
				.append(Tracker.PAGE_NAME_SEPARATOR);
		pageName.append(description);
		// Tracker.instance(getApplication()).trackPage(
		// new ArtistListMeasurement(pageName.toString(), "News", topicId));
	}

	@Override
	public boolean isRefreshable() {
		return true;
	}

	@Override
	public void refresh() {
		listAdapter.clear();
		page = 1;
		addArtists();
	}
}
