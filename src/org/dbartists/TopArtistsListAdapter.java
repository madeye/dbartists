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

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.dbartists.api.*;

import org.apache.http.client.ClientProtocolException;
import org.dbartists.api.Artist;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class TopArtistsListAdapter extends ArrayAdapter<Artist> {
	private static final String LOG_TAG = TopArtistsListAdapter.class.getName();
	private LayoutInflater inflater;

	private final static int MSG_ARTISTS_LOADED = 0;

	private ImageLoader dm;

	public TopArtistsListAdapter(Context context) {
		super(context, R.layout.artist_item);
		inflater = LayoutInflater.from(getContext());
		dm = new ImageLoader(context);
	}

	private List<Artist> moreArtists;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ARTISTS_LOADED:
				if (moreArtists != null) {
					for (Artist t : moreArtists) {
						if (getPosition(t) < 0) {
							add(t);
						}
					}
				}
				break;
			}
		}
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.artist_item, parent, false);
		}

		final Artist artist = getItem(position);

		final ImageView image = (ImageView) convertView
				.findViewById(R.id.artistItemImage);
		final TextView name = (TextView) convertView
				.findViewById(R.id.artimstItemName);
		
		ProgressBar titleProgressBar;
		titleProgressBar = (ProgressBar) parent.getRootView()
				.findViewById(R.id.leadProgressBar);
		// hide the progress bar if it is not needed
		titleProgressBar.setVisibility(ProgressBar.GONE);

		if (artist != null) {

			image.setTag(artist.getImg());
			dm.DisplayImage(artist.getImg(),
					(Activity) convertView.getContext(), image);

			name.setText(artist.getName());

		}
		return convertView;
	}

	public void addMoreArtists(final String url, final int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getMoreArtists(url, startId);
				handler.sendEmptyMessage(MSG_ARTISTS_LOADED);
			}
		}).start();
	}

	private void getMoreArtists(String url, int startId) {
		moreArtists = ArtistFactory.downloadArtists(url, startId);
		if (moreArtists != null) {
			ArtistsListActivity.addAllToArtistCache(moreArtists);
		}
	}
}
