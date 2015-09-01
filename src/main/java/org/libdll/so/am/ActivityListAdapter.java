/*	Activity Manager for Android
	Copyright 2015 libdll.so

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
*/

package org.libdll.so.am;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityListAdapter extends BaseAdapter {
	Context context;
	ActivityInfo[] activity_list;

	public ActivityListAdapter(Context context, ActivityInfo[] activities) {
		this.context = context;
		activity_list = activities;
	}

	public int getCount() {
		return activity_list.length;
	}

/*
	private String get_description(ActivityInfo info) {
		StringBuilder sb;
		if(info.)
	}
*/
	public void bindView(View view, ActivityInfo info) {
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		TextView name = (TextView)view.findViewById(R.id.text_name);
		TextView description = (TextView)view.findViewById(R.id.description);
		//icon.setImageResource(info.getIconResource());
		icon.setImageDrawable(info.loadIcon(context.getPackageManager()));
		//name.setText(info.name);
		//name.setText(info.name.substring(info.name.lastIndexOf('.')));
		name.setText(info.name.indexOf(info.packageName) == 0 ? info.name.substring(info.packageName.length()) : info.name);
		// TODO: Get other information of the activity
		//description.setText(get_description(info));
		//description.setText(info.permission);
		//description.setText(info.nonLocalizedLabel);
		//description.setText(info.loadLabel(context.getPackageManager()));
		view.setContentDescription(info.name);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View r;
		if (convertView == null) {
			r = ((LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_list_item, parent, false);
		} else {
			r = convertView;
		}
		bindView(r, activity_list[position]);
		return r;
	}

	public Object getItem(int pos) {
		return pos;
	}

	public long getItemId(int pos) {
		return pos;
	}
}
