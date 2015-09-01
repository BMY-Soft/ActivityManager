/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class ActivityListActivity extends ListActivity {
	private String package_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_activity_list);
		try {
			package_name = getIntent().getData().getSchemeSpecificPart();
			if(package_name == null) throw new NullPointerException();
		} catch(NullPointerException e) {
			Toast.makeText(this, R.string.get_package_name_failed, Toast.LENGTH_SHORT).show();
			//return;
			finish();
		}
		//Toast.makeText(this, package_name, Toast.LENGTH_SHORT).show();
		ActionBar action_bar = getActionBar();
		if(action_bar != null) {
			action_bar.setTitle(package_name);
		}
		PackageManager pm = getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			AlertDialog dialog = (new AlertDialog.Builder(this)).setTitle(package_name).setMessage(R.string.package_not_found).create();
			DialogInterface.OnCancelListener on_cancel = new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			};
			dialog.setOnCancelListener(on_cancel);
			dialog.show();
			return;
		}
		if(info.activities == null) return;
		ActivityListAdapter adapter = new ActivityListAdapter(this, info.activities);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//Toast.makeText(this, v.getContentDescription(), Toast.LENGTH_SHORT).show();

		String activity_name = v.getContentDescription().toString();
		if(activity_name.isEmpty()) return;
		Uri uri = new Uri.Builder().appendPath(package_name).appendPath(activity_name).build();
		//Intent intent = new Intent(null, Uri.fromParts("activity", activity_name, null));
		Intent intent = new Intent(null, uri);
		intent.setClass(this, ActivityControlActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
