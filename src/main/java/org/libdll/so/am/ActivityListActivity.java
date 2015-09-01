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
import android.content.pm.ActivityInfo;
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

import java.util.ArrayList;
import java.util.List;


public class ActivityListActivity extends ListActivity {
	//private String package_name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		String scheme;
		if(uri == null || (scheme = uri.getScheme()) == null) {
			Toast.makeText(this, String.format("%s\n%s", getString(R.string.get_scheme_failed), getString(R.string.unexpected_data_in_activity)), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		if(scheme.equals("package")) {
			String package_name;
			try {
				package_name = uri.getSchemeSpecificPart();
				if(package_name == null) throw new NullPointerException();
			} catch(NullPointerException e) {
				Toast.makeText(this, String.format("%s\n%s", getString(R.string.get_package_name_failed), getString(R.string.unexpected_data_in_activity)), Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			load_activities_from_package(package_name);
		} else if(scheme.equals("query")) {
			String query = uri.getSchemeSpecificPart();
			search_all_activities(query);
		} else {
			Toast.makeText(this,
				String.format("%s: %s\n%s", getString(R.string.unknown_scheme), scheme, getString(R.string.unexpected_data_in_activity)),
				Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void load_activities_from_package(String package_name) {
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
		ActivityListAdapter adapter = new ActivityListAdapter(this, info.activities, true);
		setListAdapter(adapter);
	}

	private void search_all_activities(String query) {
		setTitle(R.string.title_searching);
		PackageManager pm = getPackageManager();
		List<PackageInfo> package_list = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		ArrayList<ActivityInfo> result_list = new ArrayList<>();
		for(PackageInfo pkg_info : package_list) {
			if(pkg_info.activities == null) continue;
			for(ActivityInfo info : pkg_info.activities) {
				if(info.name.contains(query)) result_list.add(info);
			}
		}
		ActivityInfo[] activities = new ActivityInfo[result_list.size()];
		setTitle(String.format(getString(R.string.title_search_result), query));
		if(activities.length == 0) {
			Toast.makeText(this, String.format(getString(R.string.search_not_match), query), Toast.LENGTH_LONG).show();
			return;
		}
		System.arraycopy(result_list.toArray(), 0, activities, 0, activities.length);
		ActivityListAdapter adapter = new ActivityListAdapter(this, activities, false);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//Toast.makeText(this, v.getContentDescription(), Toast.LENGTH_SHORT).show();
		String component_name = v.getContentDescription().toString();
		if(component_name.isEmpty()) return;
		Uri uri = new Uri.Builder().appendPath(component_name).build();
		//Intent intent = new Intent(null, Uri.fromParts("activity", activity_name, null));
		//Intent intent = new Intent(null, uri);
		//intent.setClass(this, ActivityControlActivity.class);
		Intent intent = new Intent(this, ActivityControlActivity.class);
		intent.setData(uri);
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
