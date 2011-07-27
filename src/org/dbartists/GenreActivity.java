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

import com.flurry.android.FlurryAgent;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class GenreActivity extends PlayerActivity implements
		OnItemClickListener {
	
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "X51AT1EBV972SS9GNXTP");
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	private class GenreListAdapter extends ArrayAdapter<SubActivity> {
		public GenreListAdapter(SubActivity[] activities) {
			super(GenreActivity.this, R.layout.main_item, R.id.list_content,
					activities);
		}
	}

	private class SubActivity {
		private final Intent startIntent;

		private SubActivity(Intent startIntent) {
			this.startIntent = startIntent;
		}

		@Override
		public String toString() {

			Resources res = getResources();
			String[] genre_entries = res.getStringArray(R.array.genre_entry);

			return genre_entries[startIntent.getIntExtra(
					Constants.EXTRA_GENRE_ID, 1) - 1];
		}
	}

	private ListView listView;

	@Override
	public CharSequence getMainTitle() {
		return getString(R.string.msg_genre);
	}

	@Override
	public boolean isRefreshable() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int type = getIntent().getIntExtra(Constants.EXTRA_SUBACTIVITY_ID, -1);
		super.onCreate(savedInstanceState);
		ViewGroup container = (ViewGroup) findViewById(R.id.Content);
		View.inflate(this, R.layout.basic_list, container);
		listView = (ListView) findViewById(R.id.ListView01);

		Resources res = getResources();
		int[] genre_values = res.getIntArray(R.array.genre_value);
		SubActivity[] activities = new SubActivity[genre_values.length];

		for (int n = 0; n < genre_values.length; n++) {
			Intent t = new Intent(this, ArtistsListActivity.class);
			t.putExtra(Constants.EXTRA_GENRE_ID, genre_values[n]);
			t.putExtra(Constants.EXTRA_PAGE, 1);
			activities[n] = new SubActivity(t);
		}

		listView.setAdapter(new GenreListAdapter(activities));
		listView.setOnItemClickListener(this);

		ProgressBar titleProgressBar;
		titleProgressBar = (ProgressBar) this
				.findViewById(R.id.leadProgressBar);
		// hide the progress bar if it is not needed
		titleProgressBar.setVisibility(View.GONE);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		SubActivity s = (SubActivity) parent.getItemAtPosition(position);
		Intent i = s.startIntent;

		startActivityWithoutAnimation(i);

	}
}
