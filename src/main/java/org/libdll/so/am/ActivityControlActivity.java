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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class ActivityControlActivity extends Activity implements View.OnClickListener {
	private String activity_name;
	private String package_name;
	private ComponentName activity_component_name;

	private CheckBox check_box_data_uri;
	private EditText edit_text_uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_control);
		//activity_name = getIntent().getData().getSchemeSpecificPart();
		String path;
		try {
			path = getIntent().getData().getPath();
		} catch(NullPointerException e) {
			Toast.makeText(this, String.format("%s\n%s", getString(R.string.get_component_name_failed), getString(R.string.unexpected_data_in_activity)), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		//activity_name = getIntent().getData().getPath();
		//Log.i(uri.getPath(), uri.getPath());
		int slash_index = path.lastIndexOf('/');
		//Log.d("slash_index", String.valueOf(slash_index));
		if(slash_index <= 0) {
			//Log.d("slash_index", "abort");
			Toast.makeText(this, String.format("%s\n%s", getString(R.string.get_component_name_failed), getString(R.string.unexpected_data_in_activity)), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		activity_name = path.substring(slash_index + 1);
		package_name = path.substring(1, slash_index);
		activity_component_name = new ComponentName(package_name, activity_name);
		PackageManager pm = getPackageManager();
		ActionBar action_bar = getActionBar();
		if(action_bar != null) {
			//action_bar.setTitle(activity_name);
			action_bar.setTitle(path.substring(1));
			try {
				action_bar.setIcon(pm.getActivityIcon(activity_component_name));
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

		ActivityInfo info;
		try {
			info = pm.getActivityInfo(activity_component_name, 0);
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		TextView text_label_value = (TextView)findViewById(R.id.text_label_value);
		if(info.nonLocalizedLabel != null) text_label_value.setText(info.nonLocalizedLabel);
		else if(info.labelRes != 0) text_label_value.setText(pm.getText(package_name, info.labelRes, info.applicationInfo));

		TextView text_permission_value = (TextView)findViewById(R.id.text_permission_value);
		if(info.permission != null) text_permission_value.setText(info.permission);

		TextView text_launch_mode_value = (TextView)findViewById(R.id.text_launch_mode_value);
		switch(info.launchMode) {
			case ActivityInfo.LAUNCH_MULTIPLE:
				text_launch_mode_value.setText(R.string.launch_multiple);
				break;
			case ActivityInfo.LAUNCH_SINGLE_TOP:
				text_launch_mode_value.setText(R.string.launch_single_top);
				break;
			case ActivityInfo.LAUNCH_SINGLE_TASK:
				text_launch_mode_value.setText(R.string.launch_single_task);
				break;
			case ActivityInfo.LAUNCH_SINGLE_INSTANCE:
				text_launch_mode_value.setText(R.string.launch_single_instance);
				break;
		}

		check_box_data_uri = (CheckBox)findViewById(R.id.check_box_data_uri);
		check_box_data_uri.setOnClickListener(this);
		edit_text_uri = (EditText)findViewById(R.id.edit_text_uri);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}

	private void start_activity() {
		Intent intent = new Intent();
		//intent.setComponent(new ComponentName(package_name, activity_name));
		intent.setComponent(activity_component_name);
		if(check_box_data_uri.isChecked()) {
			Uri uri = Uri.parse(edit_text_uri.getText().toString());
			intent.setData(uri);
		}
		try {
			startActivity(intent);
		} catch(RuntimeException e) {
			(new AlertDialog.Builder(this)).setTitle(R.string.title_activity_start_failed).setMessage(e.getMessage()).show();
		}
	}

	private void start_activity_from_app_process() {
		//Runtime runtime = Runtime.getRuntime();
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
			//String command_line = String.format("app_process /system/bin com.android.commands.am.Am start %s/%s", package_name, activity_name);
			StringBuilder command_line = new StringBuilder("app_process /system/bin com.android.commands.am.Am start ");
			if(check_box_data_uri.isChecked()) command_line.append("-d '").append(edit_text_uri.getText().toString()).append("' ");
			command_line.append(package_name).append("/").append(activity_name);
			//if(check_box_data_uri.isChecked()) command_line.append(" -d ").append(edit_text_uri.getText().toString());
			shell.write_line(command_line.toString());
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
				Toast.makeText(this, "Not implemented", Toast.LENGTH_SHORT).show();
				break;
			case R.id.check_box_data_uri:
				//Log.i("check_box_data_uri", String.valueOf(check_box_data_uri.isChecked()));
				edit_text_uri.setEnabled(check_box_data_uri.isChecked());
				break;
		}
	}
}
