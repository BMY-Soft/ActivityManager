/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RunningActivityListActivity extends ListActivity {
	android.app.ActivityManager system_am;
	boolean is_search;

	private void load_running_activities() {
		//Log.d("method", "load_running_activities()");
		//ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = getPackageManager();
		List<ActivityManager.RunningTaskInfo> alltasks = system_am.getRunningTasks(Integer.MAX_VALUE);
		ActivityInfo[] activities = new ActivityInfo[alltasks.size()];
		//for(ActivityManager.RunningTaskInfo info : alltasks)
		for(int i=0; i<activities.length; i++) try {
			activities[i] = pm.getActivityInfo(alltasks.get(i).topActivity, 0);
			ActivityManager.RunningTaskInfo info = alltasks.get(i);
			Log.i(info.topActivity.getPackageName(), String.valueOf(info.numRunning));
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		ActivityListAdapter adapter = new ActivityListAdapter(this, activities, false);
		setListAdapter(adapter);
	}

	private void search_running_activities(String query) {
		setTitle(R.string.title_searching);
		PackageManager pm = getPackageManager();
		List<ActivityManager.RunningTaskInfo> alltasks = system_am.getRunningTasks(Integer.MAX_VALUE);
		ArrayList<ActivityInfo> result_list = new ArrayList<>();
		for(ActivityManager.RunningTaskInfo task_info : alltasks) try {
			ActivityInfo activity_info = pm.getActivityInfo(task_info.topActivity, 0);
			if(activity_info.name.contains(query)) result_list.add(activity_info);
		} catch(PackageManager.NameNotFoundException e) {
			//continue;
			e.printStackTrace();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		system_am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		Uri uri = getIntent().getData();
		String scheme;
		if(uri == null || (scheme = uri.getScheme()) == null) {
			load_running_activities();
			is_search = false;
		} else if(scheme.equals("query")) {
			String query = uri.getSchemeSpecificPart();
			search_running_activities(query);
			is_search = true;
		} else {
			Toast.makeText(this, String.format("%s: %s\n%s", getString(R.string.unknown_scheme), scheme, getString(R.string.unexpected_data_in_activity)), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!is_search) load_running_activities();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String component_name = v.getContentDescription().toString();
		if(component_name.isEmpty()) return;
		Uri uri = new Uri.Builder().appendPath(component_name).build();
		Intent intent = new Intent(this, ActivityControlActivity.class);
		intent.setData(uri);
		startActivity(intent);
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_running_activity_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch(id) {
			case R.id.action_reload:
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
*/
}
