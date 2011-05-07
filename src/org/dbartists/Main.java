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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main extends PlayerActivity implements OnItemClickListener {

	private class MainListAdapter extends ArrayAdapter<SubActivity> {
		public MainListAdapter(SubActivity[] activities) {
			super(Main.this, R.layout.main_item, R.id.list_content, activities);
		}
	}

	private class SubActivity {
		private final Intent startIntent;

		private SubActivity(Intent startIntent) {
			this.startIntent = startIntent;
		}

		@Override
		public String toString() {
			return Main.this.getString(startIntent.getIntExtra(
					Constants.EXTRA_SUBACTIVITY_ID, -1));
		}
	}

	private static final String LOG_TAG = Main.class.getName();

	// Main URL
	public static final String URL = "http://dbmusician.sinaapp.com/";
	private ListView listView;
	private String hourlyTitle;
	private String hourlyGuid;

	private String hourlyURL;
	private static final int MSG_ERROR_MESSAGE = 0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ERROR_MESSAGE:
				errorMessage();
				break;
			}
		}
	};

	// This is public so that we can inspect if for testing
	public List<LocationListener> locationListeners = new ArrayList<LocationListener>();

	private void errorMessage() {
		// Let the user know something was wrong, most likely a bad connection
		Toast.makeText(this,
				getResources().getString(R.string.msg_main_check_connection),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public CharSequence getMainTitle() {
		return getString(R.string.msg_main_logo);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewGroup container = (ViewGroup) findViewById(R.id.Content);
		View.inflate(this, R.layout.main_inner, container);

		listView = (ListView) findViewById(R.id.MainListView);
		String topicId = "1002";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", topicId);
		params.put("sort", "assigned");

		final SubActivity[] activities = {
				new SubActivity(new Intent(this, GenreActivity.class).putExtra(
						Constants.EXTRA_SUBACTIVITY_ID,
						R.string.msg_main_subactivity_genre)),
				new SubActivity(
						new Intent(this, TopArtistsListActivity.class)
								.putExtra(Constants.EXTRA_SUBACTIVITY_ID,
										R.string.msg_main_subactivity_top)),
				new SubActivity(
						new Intent(this, TopArtistsListActivity.class)
								.putExtra(Constants.EXTRA_SUBACTIVITY_ID,
										R.string.msg_main_subactivity_pop)),

				new SubActivity(new Intent(this,
						RecentArtistsListActivity.class).putExtra(
						Constants.EXTRA_SUBACTIVITY_ID,
						R.string.msg_main_subactivity_recent)),
						
				new SubActivity(new Intent(this,
						SearchArtistsListActivity.class).putExtra(
						Constants.EXTRA_SUBACTIVITY_ID,
						R.string.msg_main_subactivity_search)), };
		listView.setAdapter(new MainListAdapter(activities));
		listView.setOnItemClickListener(this);
		ProgressBar titleProgressBar;
		titleProgressBar = (ProgressBar) this
				.findViewById(R.id.leadProgressBar);
		// hide the progress bar if it is not needed
		titleProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SubActivity s = (SubActivity) parent.getItemAtPosition(position);
		Intent i = s.startIntent;
		startActivityWithoutAnimation(i);
	}

	private void playHourly() {
		// Request to stream audio
		// TODO: Play audio
	}

}
