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

// dbartists - Douban artists client for Android
// Copyright (C) 2011 Max Lv <max.c.lv@gmail.com>
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License.  You may obtain a copy
// of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
// License for the specific language governing permissions and limitations
// under the License.
//
//
//                           ___====-_  _-====___
//                     _--^^^#####//      \\#####^^^--_
//                  _-^##########// (    ) \\##########^-_
//                 -############//  |\^^/|  \\############-
//               _/############//   (@::@)   \\############\_
//              /#############((     \\//     ))#############\
//             -###############\\    (oo)    //###############-
//            -#################\\  / VV \  //#################-
//           -###################\\/      \//###################-
//          _#/|##########/\######(   /\   )######/\##########|\#_
//          |/ |#/\#/\#/\/  \#/\##\  |  |  /##/\#/  \/\#/\#/\#| \|
//          `  |/  V  V  `   V  \#\| |  | |/#/  V   '  V  V  \|  '
//             `   `  `      `   / | |  | | \   '      '  '   '
//                              (  | |  | |  )
//                             __\ | |  | | /__
//                            (vvv(VVV)(VVV)vvv)
//
//                             HERE BE DRAGONS

package org.dbartists;

import java.util.List;

import org.dbartists.ArtistsListAdapter.ViewHolder;
import org.dbartists.api.Artist;
import org.dbartists.api.ArtistFactory;

import android.app.Activity;
import android.content.Context;
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

public class SearchArtistsListAdapter extends ArrayAdapter<Artist> {
	private static final String LOG_TAG = SearchArtistsListAdapter.class
			.getName();
	private LayoutInflater inflater;

	private final static int MSG_ARTISTS_LOADED = 0;

	private boolean finish = false;

	private ImageLoader dm;

	private List<Artist> moreArtists;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ARTISTS_LOADED:
				if (moreArtists != null) {
					remove(null);
					for (Artist t : moreArtists) {
						if (getPosition(t) < 0) {
							add(t);
						}
					}
					add(null);
				}
				break;
			}
		}
	};

	public SearchArtistsListAdapter(Context context) {
		super(context, R.layout.artist_item);
		inflater = LayoutInflater.from(getContext());
		dm = ImageLoaderFactory.getImageLoader(context);
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
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.artist_item, parent, false);
			holder.image = (ImageView) convertView
					.findViewById(R.id.artistItemImage);
			holder.name = (TextView) convertView
					.findViewById(R.id.artimstItemName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Artist artist = getItem(position);

		if (!finish) {
			ProgressBar titleProgressBar;
			titleProgressBar = (ProgressBar) parent.getRootView().findViewById(
					R.id.leadProgressBar);
			// hide the progress bar if it is not needed
			titleProgressBar.setVisibility(View.GONE);
			finish = true;
		}

		if (artist != null) {

			holder.image.setTag(artist.getImg());
			holder.image.setVisibility(View.VISIBLE);
			dm.DisplayImage(artist.getImg(),
					(Activity) convertView.getContext(), holder.image);

			holder.name.setText(artist.getName());

		} else {
			// null marker means it's the end of the list.
			Log.d(LOG_TAG, "search more");
			holder.image.setVisibility(View.INVISIBLE);
			holder.name.setText(R.string.msg_search_more);
		}
		return convertView;
	}
}
