/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.activity_about);
		//Window window = getWindow();
		//window.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView text_version = (TextView)findViewById(R.id.text_version);
			text_version.setText("Version " + info.versionName);
		} catch(PackageManager.NameNotFoundException e) {
			Toast.makeText(this, R.string.package_not_found, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

}
