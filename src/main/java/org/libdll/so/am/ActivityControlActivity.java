package org.libdll.so.am;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class ActivityControlActivity extends Activity implements View.OnClickListener {
	private String activity_name;
	private String package_name;
	private ComponentName activity_component_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_control);
		//activity_name = getIntent().getData().getSchemeSpecificPart();
		String path;
		try {
			path = getIntent().getData().getPath();
		} catch(NullPointerException e) {
			Toast.makeText(this, R.string.get_package_name_failed, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		//activity_name = getIntent().getData().getPath();
		//Log.i(uri.getPath(), uri.getPath());
		activity_name = path.substring(path.lastIndexOf('/') + 1);
		package_name = path.substring(1, path.lastIndexOf('/'));
		activity_component_name = new ComponentName(package_name, activity_name);
		ActionBar action_bar = getActionBar();
		if(action_bar != null) {
			//action_bar.setTitle(activity_name);
			action_bar.setTitle(path.substring(1));
			try {
				action_bar.setIcon(getPackageManager().getActivityIcon(activity_component_name));
			} catch(PackageManager.NameNotFoundException e) {
				Toast.makeText(this, R.string.package_not_found, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		Button button_start = (Button)findViewById(R.id.button_start);
		Button button_start_with_root = (Button)findViewById(R.id.button_start_with_root);
		Button button_killall = (Button)findViewById(R.id.button_killall);
		button_start.setOnClickListener(this);
		button_start_with_root.setOnClickListener(this);
		button_killall.setOnClickListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
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

	private void start_activity() {
		Intent intent = new Intent();
		//intent.setComponent(new ComponentName(package_name, activity_name));
		intent.setComponent(activity_component_name);
		try {
			startActivity(intent);
		} catch(RuntimeException e) {
			(new AlertDialog.Builder(this)).setTitle(R.string.title_activity_start_failed).setMessage(e.getMessage()).show();
		}
	}

	private void start_activity_from_app_process() {
		Runtime runtime = Runtime.getRuntime();
		try {
			String[] environ = new String[] {
				"BOOTCLASSPATH=/system/framework/core.jar:/system/framework/core-junit.jar:/system/framework/bouncycastle.jar:/system/framework/ext.jar:/system/framework/framework.jar:/system/framework/telephony-common.jar:/system/framework/mms-common.jar:/system/framework/android.policy.jar:/system/framework/services.jar:/system/framework/apache-xml.jar:/system/framework/com.intel.multidisplay.jar",
				"CLASSPATH=/system/framework/am.jar"
			};
			runtime.exec(String.format("app_process /system/bin com.android.commands.am.Am start %s/%s", package_name, activity_name), environ, null);
		} catch(IOException e) {
			e.printStackTrace();
			(new AlertDialog.Builder(this)).setTitle(R.string.title_activity_start_failed).setMessage(e.getMessage()).show();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
			case R.id.button_start:
				start_activity();
				break;
			case R.id.button_start_with_root:
				start_activity_from_app_process();
				break;
			case R.id.button_killall:
				break;
		}
	}
}
