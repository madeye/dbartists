// Copyright 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.dbartists;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.dbartists.utils.PlaylistEntry;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * @author mfrederick@google.com (Michael Frederick)
 * 
 *         A base class for all Activities that want to display the default
 *         layout, including the ListenView.
 */
public abstract class PlayerActivity extends ActivityGroup implements
		Refreshable {
	protected TextView titleText;

	public abstract CharSequence getMainTitle();

	private static final String LOG_TAG = PlayerActivity.class.getName();
	private ListenView listenView;
	private static boolean ignoreWifi = false;
	private static boolean closeAd = false;

	private int getVersionCode() {
		int version = -1;
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version = pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return version;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Override the normal volume controls so that the user can alter the
		// volume
		// when a stream is not playing.
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);
		titleText = (TextView) findViewById(R.id.LogoNavText);
		titleText.setText(getMainTitle());

		if (!closeAd) {
			// Create the adView
			AdView adView = new AdView(this, AdSize.BANNER, "a14dbe96b16a891");
			// Lookup your LinearLayout assuming it¡¯s been given
			// the attribute android:id="@+id/mainLayout"
			LinearLayout layout = (LinearLayout) findViewById(R.id.ad);
			// Add the adView to it
			layout.addView(adView);
			// Initiate a generic request to load it with an ad
			AdRequest aq = new AdRequest();
			// aq.setTesting(true);
			adView.loadAd(aq);
		}

		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && !networkInfo.getTypeName().equals("WIFI")) {
			if (!ignoreWifi) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.msg_alert_wifi)
						.setCancelable(false)
						.setTitle(getString(R.string.msg_allert))
						.setPositiveButton(R.string.msg_yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										ignoreWifi = true;
										dialog.cancel();
									}
								})
						.setNegativeButton(R.string.msg_no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										PlayerActivity.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		listenView = new ListenView(this);
		((ViewGroup) findViewById(R.id.MediaPlayer)).addView(listenView,
				new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
	}

	@Override
	public boolean isRefreshable() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void finish() {
		super.finish();
		noAnimation();
	}

	protected void startActivityWithoutAnimation(Intent i) {
		startActivity(i);
		noAnimation();
	}

	/**
	 * Prevents the default animation on the pending transition. Only works on
	 * SDK version 5 and up, but may be safely called from any version.
	 */
	protected void noAnimation() {
		try {
			Method overridePendingTransition = Activity.class.getMethod(
					"overridePendingTransition", new Class[] { int.class,
							int.class });
			overridePendingTransition.invoke(this, 0, 0);
		} catch (SecurityException e) {
			Log.w(LOG_TAG, "", e);
		} catch (NoSuchMethodException e) {
			// Don't log an error here; we anticipate an error on SDK < 5
		} catch (IllegalArgumentException e) {
			Log.w(LOG_TAG, "", e);
		} catch (IllegalAccessException e) {
			Log.w(LOG_TAG, "", e);
		} catch (InvocationTargetException e) {
			Log.w(LOG_TAG, "", e);
		}
	}

	private enum MenuId {
		ABOUT, REFRESH, CLOSEAD
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MenuId.ABOUT.ordinal(), Menu.NONE,
				R.string.msg_main_menu_about)
				.setIcon(android.R.drawable.ic_menu_help)
				.setAlphabeticShortcut('a');
		if (this.isRefreshable()) {
			menu.add(Menu.NONE, MenuId.REFRESH.ordinal(), Menu.NONE,
					R.string.msg_refresh).setAlphabeticShortcut('r')
					.setIcon(R.drawable.reload);
		}
		menu.add(Menu.NONE, MenuId.CLOSEAD.ordinal(), Menu.NONE,
				R.string.msg_close_ad)
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setAlphabeticShortcut('c');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MenuId.ABOUT.ordinal()) {
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		} else if (item.getItemId() == MenuId.REFRESH.ordinal()) {
			this.refresh();
		} else if (item.getItemId() == MenuId.CLOSEAD.ordinal()) {
			this.closeAd();
		}
		return super.onOptionsItemSelected(item);
	}

	private void closeAd() {
		if (closeAd)
			closeAd = false;
		else
			closeAd = true;
		LinearLayout layout = (LinearLayout) findViewById(R.id.ad);
		layout.setVisibility(LinearLayout.GONE);
	}

	protected void listen(PlaylistEntry entry) {
		listenView.listen(entry);
	}

	protected void addToPlayList(List<PlaylistEntry> entries) {
		listenView.addToPlayList(entries);
	}
}
