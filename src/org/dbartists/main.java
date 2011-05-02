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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbartists.utils.Tracker;
import org.dbartists.utils.Tracker.ActivityMeasurement;

public class Main extends PlayerActivity implements OnItemClickListener {

	private static final String LOG_TAG = Main.class.getName();

	// Main URL
	public static final String URL = "http://dbmusician.sinaapp.com/";

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

	private ListView listView;
	private String hourlyTitle;
	private String hourlyGuid;
	private String hourlyURL;

	private static final int MSG_ERROR_MESSAGE = 0;
	private static final int MSG_PLAY_HOURLY = 1;
	private static final int MSG_CANCEL_LOCATION_LISTENERS = 2;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ERROR_MESSAGE:
				errorMessage();
				break;
			case MSG_PLAY_HOURLY:
				playHourly();
				break;
			case MSG_CANCEL_LOCATION_LISTENERS:
				break;
			}
		}
	};

	// This is public so that we can inspect if for testing
	public List<LocationListener> locationListeners = new ArrayList<LocationListener>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewGroup container = (ViewGroup) findViewById(R.id.Content);
		ViewGroup.inflate(this, R.layout.main_inner, container);

		listView = (ListView) findViewById(R.id.MainListView);
		String grouping = null;
		String description = "Top Stories";
		String topicId = "1002";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", topicId);
		params.put("sort", "assigned");

		final SubActivity[] activities = {
				// new SubActivity(new Intent(this,
				// TrackListActivity.class).putExtra(
				// Constants.EXTRA_SUBACTIVITY_ID,
				// R.string.msg_main_subactivity_news)
				// .putExtra(Constants.EXTRA_QUERY_URL, newsUrl)
				// .putExtra(Constants.EXTRA_DESCRIPTION, description)
				// .putExtra(Constants.EXTRA_GROUPING, grouping)
				// .putExtra(Constants.EXTRA_SIZE, 5)),
				new SubActivity(new Intent(this, GenreActivity.class).putExtra(
						Constants.EXTRA_SUBACTIVITY_ID,
						R.string.msg_main_subactivity_genre)),
				new SubActivity(new Intent(this, GenreActivity.class).putExtra(
						Constants.EXTRA_SUBACTIVITY_ID,
						R.string.msg_main_subactivity_programs)),
		// new SubActivity(new Intent(this, StationListActivity.class).putExtra(
		// Constants.EXTRA_SUBACTIVITY_ID,
		// R.string.msg_main_subactivity_stations)),
		// new SubActivity(new Intent(this, SearchActivity.class).putExtra(
		// Constants.EXTRA_SUBACTIVITY_ID,
		// R.string.msg_main_subactivity_search))
		};
		listView.setAdapter(new MainListAdapter(activities));
		listView.setOnItemClickListener(this);
		trackNow();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SubActivity s = (SubActivity) parent.getItemAtPosition(position);
		Intent i = s.startIntent;
		startActivityWithoutAnimation(i);
	}

	private class MainListAdapter extends ArrayAdapter<SubActivity> {
		public MainListAdapter(SubActivity[] activities) {
			super(Main.this, android.R.layout.simple_list_item_1,
					android.R.id.text1, activities);
		}
	}

	@Override
	public CharSequence getMainTitle() {
		return getString(R.string.msg_main_logo);
	}

	@Override
	public void trackNow() {
		StringBuilder pageName = new StringBuilder("Home Screen");
		Tracker.instance(getApplication()).trackPage(
				new ActivityMeasurement(pageName.toString(), "Home"));
	}

	private void playHourly() {
		// Request to stream audio
		// TODO: Play audio
	}

	private void errorMessage() {
		// Let the user know something was wrong, most likely a bad connection
		Toast.makeText(this,
				getResources().getString(R.string.msg_main_check_connection),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Tracker.instance(getApplication()).finish();
	}

}