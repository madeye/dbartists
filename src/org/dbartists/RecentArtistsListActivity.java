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

import org.dbartists.api.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RecentArtistsListActivity extends PlayerActivity implements
		OnItemClickListener {

	private final static String TAG = "RecentArtistsListActivity";
	private String description;

	protected RecentArtistsListAdapter listAdapter;

	private void addArtists() {

		listAdapter.addMoreArtists();

	}

	@Override
	public CharSequence getMainTitle() {
		Log.d(TAG, description);
		return description;
	}

	@Override
	public boolean isRefreshable() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		int type = getIntent().getIntExtra(Constants.EXTRA_SUBACTIVITY_ID, 1);
		description = getString(type);

		super.onCreate(savedInstanceState);
		ViewGroup container = (ViewGroup) findViewById(R.id.Content);
		View.inflate(this, R.layout.basic_list, container);

		ListView listView = (ListView) findViewById(R.id.ListView01);
		listView.setOnItemClickListener(this);
		listAdapter = new RecentArtistsListAdapter(RecentArtistsListActivity.this);
		listView.setAdapter(listAdapter);

		addArtists();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Artist s = (Artist) parent.getAdapter().getItem(position);
		Intent i = new Intent(this, TrackListActivity.class);
		i.putExtra(Constants.EXTRA_ARTIST_NAME, s.getName());
		i.putExtra(Constants.EXTRA_ARTIST_IMG, s.getImg());
		i.putExtra(Constants.EXTRA_ARTIST_URL, s.getUrl());
		startActivityWithoutAnimation(i);
	}

	@Override
	public void refresh() {
		listAdapter.clear();
		addArtists();
	}
}
