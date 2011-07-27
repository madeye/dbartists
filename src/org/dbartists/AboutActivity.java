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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends Activity {

	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "X51AT1EBV972SS9GNXTP");
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

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

	private String getVersionName() {
		String version = "";
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version = pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			version = "Package name not found";
		}
		return version;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView tv = (TextView) findViewById(R.id.AboutText);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(getString(R.string.msg_about_developer),
				getString(R.string.msg_about_developer_value));
		map.put(getString(R.string.msg_about_contact),
				getString(R.string.msg_about_contact_value));
		map.put(getString(R.string.msg_about_version_name), getVersionName());
		map.put(getString(R.string.msg_about_version_code), ""
				+ getVersionCode());
		populateField(map, tv);
	}

	private void populateField(Map<String, String> values, TextView view) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : values.entrySet()) {
			String fieldName = entry.getKey();
			String fieldValue = entry.getValue();
			sb.append(fieldName).append(": ").append("<b>").append(fieldValue)
					.append("</b><br>");
		}
		view.setText(Html.fromHtml(sb.toString()));
	}
}
