/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
				//e.printStackTrace();
				Toast.makeText(this, R.string.component_not_found, Toast.LENGTH_SHORT).show();
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
			/*
			String[] args = new String[] {
				"app_process",
				"/system/bin",
				"com.android.commands.am.Am",
				"start",
				String.format("%s/%s", package_name, activity_name)
			};

			String[] environ = new String[] {
				"PATH=/system/bin:/sbin",
				"LD_LIBRARY_PATH=/system/lib",
				"BOOTCLASSPATH=" + System.getProperty("java.boot.class.path"),
				"CLASSPATH=/system/framework/am.jar"
			};*/
			/*
			Map<String, String> env_map = System.getenv();
			//env_map.put("CLASSPATH", "/system/framework/am.jar");
			//Set<String> key_set = env_map.keySet();
			//Set<String> value_set = env_map.
			Set<Map.Entry<String, String>> env_set = env_map.entrySet();
		*/	/*
			env_set.add(new Map.Entry<String, String>() {
				@Override
				public String getKey() {
					return "CLASSPATH";
				}

				@Override
				public String getValue() {
					return "/system/framework/am.jar";
				}

				@Override
				public String setValue(String object) {
					return null;
				}
			});*//*
			//Map.Entry<String, String>[] env_entries = (Map.Entry<String, String>[])env_set.toArray();
			Object[] env_entries = env_set.toArray();
			String[] environ = new String[env_map.size() + 2];
			for(int i=0; i<environ.length-2; i++) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>)env_entries[i];
				environ[i] = String.format("%s=%s", entry.getKey(), entry.getValue());
				Log.d(entry.getKey(), entry.getValue());
			}
			environ[environ.length - 2] = "LD_LIBRARY_PATH=/system/lib";
			environ[environ.length - 1] = "CLASSPATH=/system/framework/am.jar";

			*/
			//runtime.exec(args, environ);
			//runtime.exec(String.format("app_process /system/bin com.android.commands.am.Am start %s/%s", package_name, activity_name), environ, null);

/*
			Process root_shell = runtime.exec("su");
			DataOutputStream shell_stdin = new DataOutputStream(root_shell.getOutputStream());
			DataInputStream shell_stderr = new DataInputStream(root_shell.getErrorStream());
			//byte[] buffer = new byte[1024];
			Log.i("shell_stderr available", String.valueOf(shell_stderr.available()));
			//Toast.makeText(this, shell_stderr.readUTF(), Toast.LENGTH_SHORT).show();
			//new String()
			//BufferedInputStream buffered_shell_stderr = new BufferedInputStream(root_shell.getErrorStream());
			//buffered_shell_stderr.read(buffer)
			//java.nio.ByteBuffer.allocate().
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			ReadableByteChannel shell_stderr_channel = Channels.newChannel(shell_stderr);
			Log.i("shell_stderr_channel", shell_stderr_channel.toString());
			shell_stderr_channel.read(buffer);
			Log.i("buffer", buffer.toString());
			shell_stderr.
*/

			RootShell shell = RootShell.get_instance(this);
			if(shell == null) return;
			shell.write_line("export PATH=/system/bin:/sbin");
			shell.write_line("export LD_LIBRARY_PATH=/system/lib");
			shell.write_line("export CLASSPATH=/system/framework/am.jar");
			shell.write_line(String.format("app_process /system/bin com.android.commands.am.Am start %s/%s", package_name, activity_name));
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
