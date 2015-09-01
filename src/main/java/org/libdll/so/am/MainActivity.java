/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTabHost;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TabHost;

public class MainActivity extends TabActivity implements SearchView.OnQueryTextListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		/*
		Intent intent = new Intent();
		//intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.MyTabActivity"));
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsGps"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsView"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsCpAutoTest"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsEpo"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsMap"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsNet"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.LbsMisc"));
		startActivity(intent);
		intent.setComponent(new ComponentName("com.mediatek.lbs.em", "com.mediatek.lbs.em.FileBrowser"));
		startActivity(intent);
		*/
		/*
		FragmentTabHost tabhost = (FragmentTabHost)findViewById(R.id.tabhost);
		if(tabhost == null) throw new RuntimeException("TabHost ???");
		//tabhost.setup();
		tabhost.setup(this, getSupportFragmentManager());
		*/
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		//ActionBar actionbar = getActionBar();
		//actionbar.show();
		TabHost tabhost = getTabHost();
		//TabHost tabhost = (TabHost)findViewById(R.id.tabhost);
		//tabhost.setup();

		tabhost.addTab(tabhost.newTabSpec("all").setIndicator("All").setContent(new Intent(this, PackageListActivity.class)));
		tabhost.addTab(tabhost.newTabSpec("running").setIndicator("Running").setContent(new Intent(this, RunningActivityListActivity.class)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		SearchView search_view = (SearchView)menu.findItem(R.id.action_search).getActionView();
		search_view.setOnQueryTextListener(this);
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

	public boolean onQueryTextSubmit(String query) {
		//Toast.makeText(this, "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();

		return true;
	}

	public boolean onQueryTextChange(String new_text) {
		return false;
	}

}
