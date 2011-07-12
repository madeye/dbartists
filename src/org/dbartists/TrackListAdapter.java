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

import org.dbartists.api.Track;
import org.dbartists.api.TrackFactory;
import org.dbartists.utils.PlaylistProvider;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrackListAdapter extends ArrayAdapter<Track> {

	private static final String TAG = TrackListAdapter.class.getName();
	private LayoutInflater inflater;

	private List<Track> moreTracks;
	private boolean finish = false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (moreTracks != null) {
				if (moreTracks.size() > 0) {
					remove(null);
					for (Track s : moreTracks) {
						if (getPosition(s) < 0) {
							add(s);
						}
					}
				} else {
					clear();
					add(null);
				}
			}
		}
	};

	public TrackListAdapter(Context context) {
		super(context, R.layout.track_item);
		inflater = LayoutInflater.from(getContext());
	}

	public void addMoreTracks(final String url, final int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getMoreTracks(url, startId);
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	private void getMoreTracks(String url, int startId) {
		moreTracks = TrackFactory.downloadTracks(url, startId);
	}

	public List<Track> getTracklist() {
		return moreTracks;
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
			convertView = inflater.inflate(R.layout.track_item, parent, false);
			holder.image = (ImageView) convertView
					.findViewById(R.id.TrackItemStatusImage);
			holder.name = (TextView) convertView
					.findViewById(R.id.TrackItemNameText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Track track = getItem(position);

		if (!finish) {
			ProgressBar titleProgressBar;
			titleProgressBar = (ProgressBar) parent.getRootView().findViewById(
					R.id.leadProgressBar);
			// hide the progress bar if it is not needed
			titleProgressBar.setVisibility(View.GONE);
			finish = true;
		}

		if (track != null) {
			// image.setImageDrawable(getContext().getResources().getDrawable(
			// isPlayable(story) ? R.drawable.icon_listen_main
			// : R.drawable.bullet));
			if (isPlaying(track.getName()))
				holder.image.setImageResource(R.drawable.icon_listen_main);
			else
				holder.image.setImageResource(R.drawable.icon_item);
			holder.image.setVisibility(View.VISIBLE);
			holder.name.setText(track.getName());
		} else {
			holder.image.setTag("null");
			holder.image.setVisibility(View.INVISIBLE);
			holder.name.setText(R.string.msg_alert_load_failed);
		}
		return convertView;
	}

	private boolean isDownloaded(Track track) {
		// TODO: implement download manager
		return false;
	}

	private boolean isPlaying(String name) {

		String selection = PlaylistProvider.Items.NAME + " = ? and "
				+ PlaylistProvider.Items.IS_PLAYING + " = ?";
		String[] selectionArgs = new String[2];
		selectionArgs[0] = name;
		selectionArgs[1] = "1";
		String sortOrder = PlaylistProvider.Items.PLAY_ORDER + " asc";

		Cursor c = getContext().getContentResolver().query(
				PlaylistProvider.CONTENT_URI, null, selection, selectionArgs,
				sortOrder);
		if (c.moveToFirst()) {
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	public void refresh() {
		clear();
		if (moreTracks != null) {
			for (Track s : moreTracks) {
				if (getPosition(s) < 0) {
					add(s);
				}
			}
		}
	}
}
