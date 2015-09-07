/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

class DialogResultHandler extends Handler {
	@Override
	public void handleMessage(Message msg) {
		throw new RuntimeException();
		//switch(msg.what
		//Looper.getMainLooper().quit();
	}
}

public class RootShell {
	private RootShell() throws IOException, InterruptedException {
		process = Runtime.getRuntime().exec("su");
		shell_stdin = new DataOutputStream(process.getOutputStream());
		shell_stdout = new DataInputStream(process.getInputStream());
		shell_stderr = new DataInputStream(process.getErrorStream());
		for(int i=0; i<100; i++) try {
			Thread.sleep(10);
			Log.i("shell_stdout available", String.valueOf(shell_stdout.available()));
			if(shell_stdout.available() > 0) break;
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[32];
		if(shell_stdout.available() == 0) try {
			int status = process.exitValue();
			//String msg = String.format("shell terminated with status %d", status);
			StringBuilder msg = new StringBuilder();
			if(shell_stderr.available() > 0) {
				if(shell_stderr.read(buffer) > 0) {
					//msg = new String(buffer);
					msg.append(new String(buffer));
				}
			}
			msg.append(String.format(context.getString(R.string.shell_terminated), status));
			throw new IOException(msg.toString());
		} catch(IllegalThreadStateException e) {
			// Still running
			//Thread.sleep(100);
			return;
		}
		if(shell_stdout.read(buffer) < 1) return;
		String result = new String(buffer);
		//Log.i("result", result.substring(0, 9));
		//int count = 0;
		if(result.substring(0, 9).equals("Password:")) {
			Log.w("RootShell", "su is require a password");
/*
			final android.os.Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					//throw new RuntimeException();

					Looper.getMainLooper().quit();
				}
			};
*/
			final DialogResultHandler handler = new DialogResultHandler();
			//final StringBuffer pw = new StringBuffer();
			//final Boolean ok = new Boolean();
			//final Boolean ok = new Boolean(false);
			//final Boolean ok = Boolean.valueOf(false);
			//View text_entry = LayoutInflater.from(context).inflate()
			TextView text_view = new TextView(context);
			text_view.setText(R.string.password_for_root);
			text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			final EditText password_entry = new EditText(context);
			//password_entry.setTransformationMethod(PasswordTransformationMethod.getInstance());
			password_entry.setSingleLine(true);
			password_entry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(text_view);
			layout.addView(password_entry);
			//if(context == null) throw new RuntimeException("context ???");
			AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
			dialog_builder.setTitle(R.string.title_run_as_root);
			dialog_builder.setView(layout);
			DialogInterface.OnClickListener on_ok_click_listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//pw.append(password_entry.getText().toString());
					password = password_entry.getText().toString();
					handler.sendEmptyMessage(1);
					//handler.sendMessage(new Message());
					/*
					Bundle b = new Bundle();
					b.putString("password", password_entry.getText().toString());
					Message message = new Message();
					message.setData(b);
					handler.sendMessage(message);
					*/
				}
			};
			DialogInterface.OnClickListener on_cancel_click_listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					password = null;
					handler.sendEmptyMessage(0);
					//handler.sendMessage(new Message());
					//handler.sendMessage(handler.obtainMessage());
				}
			};
			DialogInterface.OnCancelListener on_cancel_listener = new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					password = null;
					handler.sendEmptyMessage(0);
				}
			};
			dialog_builder.setPositiveButton(R.string.action_ok, on_ok_click_listener);
			dialog_builder.setNegativeButton(R.string.action_cancel, on_cancel_click_listener);
			dialog_builder.setOnCancelListener(on_cancel_listener);
			dialog_builder.create().show();
			//Intent intent = new Intent(null, PasswordActivity.class);
			//intent.setClass();
			//context.startActivity();
			try {
				Looper.loop();
			} catch(RuntimeException e) {
				e.printStackTrace();
			}
			//Log.i("RootShell", password);
			//Toast.makeText(context, "No password?", Toast.LENGTH_SHORT).show();
			if(password == null) {
				//Log.i("RootShell", "give up on password");
				terminate();
				instance = null;
				//return;
				//throw new IOException("Authentication failure");
				throw new InterruptedException();
			}
			shell_stdin.writeBytes(password);
			password = null;
			shell_stdin.write('\n');
			System.gc();

			for(int i = 0; i < 100; i++) try {
				Thread.sleep(10);
				Log.i("shell_stdout available", String.valueOf(shell_stderr.available()));
				if(shell_stderr.available() > 0) break;
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			if(shell_stderr.available() == 0) return;
			int s = shell_stderr.read(buffer);
			if(s < 1) return;
			Log.i("RootShell", String.format("s = %d", s));
			result = new String(buffer, 0, s);
			Log.i("RootShell", result);
			//if(s != 23) return;
			//if(++count > 3) throw new IOException("Authentication failure");
			if(s == 23 && result.equals("su: incorrect password\n")) {
				throw new IOException(context.getString(R.string.authentication_failure));
			}
		}
		//} while(result.equals("su: incorrect password\n"));


		/*
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		ReadableByteChannel shell_stderr_channel = Channels.newChannel(shell_stdout);
		Log.i("shell_stderr_channel", shell_stderr_channel.toString());
		shell_stderr_channel.read(buffer);
		Log.i("buffer", buffer.toString());
		*/
		/*
		while(true) Log.i("shell_stderr available", String.valueOf(shell_stderr.available()));
		int c = shell_stdout.read();
		Log.i("First char", String.valueOf((char)c));
		*/
	}

	private static RootShell instance;
	private Process process;
	private DataOutputStream shell_stdin;
	private DataInputStream shell_stdout;
	private DataInputStream shell_stderr;
	private static Context context;

	private String password;

	private boolean is_terminated() {
		try {
			process.exitValue();
			return true;
		} catch(IllegalThreadStateException e) {
			//e.printStackTrace();
			return false;
		}
	}

	public void terminate() {
		process.destroy();
	}

	public void quit() {
		if(is_terminated()) return;
		try {
			//shell_stdin.writeBytes("exit\n");
			write_line("exit");
		} catch(IOException e) {
			e.printStackTrace();
		}
		terminate();
	}

	public static RootShell get_instance(Context context) throws IOException {
		RootShell.context = context;
		try {
			if(instance == null || instance.is_terminated()) instance = new RootShell();
		} catch(InterruptedException e) {
			instance = null;
		}
		return instance;
	}

	public void write_line(String line) throws IOException {
		Log.d("method", String.format("org.libdll.so.am.RootShell::write_line(String<%s>)", line));
		//shell_stdin.writeBytes(line);
		shell_stdin.write(line.getBytes());	// Using default charset UTF-8
		shell_stdin.write('\n');
	}
}
